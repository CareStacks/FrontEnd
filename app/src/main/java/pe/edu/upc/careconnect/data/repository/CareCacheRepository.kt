package pe.edu.upc.careconnect.data.repository

import android.content.Context
import android.net.Uri
import java.io.File
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import pe.edu.upc.careconnect.data.local.CachedDiaryNoteEntity
import pe.edu.upc.careconnect.data.local.CachedDocumentEntity
import pe.edu.upc.careconnect.data.local.CachedNotificationEntity
import pe.edu.upc.careconnect.data.local.CareCacheDatabase
import pe.edu.upc.careconnect.data.remote.ApiClient
import pe.edu.upc.careconnect.data.remote.DocumentItemDto
import pe.edu.upc.careconnect.data.remote.toUserMessage
import pe.edu.upc.careconnect.data.session.SessionManager

class CareCacheRepository private constructor(
    private val context: Context,
    private val database: CareCacheDatabase
) {
    private val dao = database.careCacheDao()
    private val api = ApiClient.service
    private val sessionManager = SessionManager.getInstance(context)
    private val uploadScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val documents = dao.observeDocuments()
    val diaryNotes = dao.observeDiaryNotes()
    val notifications = dao.observeNotifications()

    suspend fun syncDocuments() {
        val localPendingDocuments = dao.getPendingDocuments()
        val patientId = sessionManager.requireActivePatientId()
        val remoteDocuments = api.getDocumentsByPatient(patientId)
        val cachedDocuments = remoteDocuments
            .flatMap { medicalDocument ->
                if (sessionManager.medicalDocumentId == null) {
                    sessionManager.saveMedicalDocumentId(medicalDocument.id)
                }

                medicalDocument.documentItems.mapIndexed { index, item ->
                    CachedDocumentEntity(
                        id = item.id.toString(),
                        title = item.title,
                        backendDocumentType = item.documentType,
                        type = item.documentType.toDisplayDocumentType(),
                        date = item.uploadedAt.toReadableDate(),
                        section = if (index < 3) "RECIENTES" else "HISTORIAL ANUAL",
                        tone = item.documentType.toDocumentTone(),
                        description = item.description.orEmpty(),
                        sortOrder = -(item.id),
                        localUri = null,
                        storageBucket = item.storageBucket.orEmpty(),
                        storagePath = item.storagePath.orEmpty(),
                        mimeType = item.mimeType,
                        fileSizeBytes = item.fileSizeBytes,
                        uploadedAt = item.uploadedAt,
                        syncStatus = item.syncStatus ?: DOCUMENT_STATUS_SYNCED,
                        errorMessage = null
                    )
                }
            }

        dao.clearSyncedDocuments()
        if (cachedDocuments.isNotEmpty()) {
            dao.upsertDocuments(cachedDocuments)
        }
        if (localPendingDocuments.isNotEmpty()) {
            dao.upsertDocuments(localPendingDocuments)
        }
    }

    suspend fun syncDiaryNotes() {
        val patientId = sessionManager.requireActivePatientId()
        val remoteNotes = api.getDiaryEntriesByPatient(patientId)
        val cachedNotes = remoteNotes.mapIndexed { index, note ->
            CachedDiaryNoteEntity(
                id = note.id.toString(),
                author = sessionManager.userFullName ?: "CareConnect",
                timestamp = note.entryDate.toReadableDateTime(),
                body = note.content,
                tags = if (index == 0) "Nueva nota" else "",
                tone = if (index == 0) "green" else "neutral",
                isHighlighted = index == 0,
                sortOrder = -note.id
            )
        }

        dao.clearDiaryNotes()
        if (cachedNotes.isNotEmpty()) {
            dao.upsertDiaryNotes(cachedNotes)
        }
    }

    suspend fun syncNotifications() {
        val recipientId = sessionManager.requireCurrentUserId()
        val remoteNotifications = api.getNotificationsByRecipient(recipientId)
        val cachedNotifications = remoteNotifications.map { notification ->
            CachedNotificationEntity(
                id = notification.id,
                title = notification.title,
                message = notification.message,
                status = notification.status.toDisplayStatus(),
                statusTone = notification.status.toStatusTone(),
                actionLabel = notification.type.toActionLabel(),
                timestamp = (notification.readAt ?: notification.sentAt ?: notification.createdAt).toReadableDateTime(),
                iconTone = notification.priority.toIconTone(),
                sortOrder = -((notification.createdAt ?: "0").hashCode().toLong())
            )
        }

        dao.clearNotifications()
        if (cachedNotifications.isNotEmpty()) {
            dao.upsertNotifications(cachedNotifications)
        }
    }

    suspend fun saveDiaryNote(
        title: String,
        body: String
    ) {
        val noteBody = buildString {
            if (title.isNotBlank()) {
                append(title.trim())
                append(". ")
            }
            append(body.ifBlank { "Nota registrada sin contenido adicional." })
        }

        api.createDiaryEntry(
            pe.edu.upc.careconnect.data.remote.CreateDiaryEntryRequestDto(
                patientId = sessionManager.requireActivePatientId(),
                content = noteBody
            )
        )

        syncDiaryNotes()
    }

    suspend fun saveDocument(
        documentType: String,
        title: String,
        description: String,
        fileUri: Uri,
        mimeType: String,
        fileSizeBytes: Long,
        uploadedAt: LocalDateTime?
    ) {
        if (fileSizeBytes > MAX_DOCUMENT_SIZE_BYTES) {
            throw IllegalArgumentException("El archivo supera el tamaño permitido.")
        }

        val localId = "local-${UUID.randomUUID()}"
        val uploadDate = uploadedAt ?: LocalDateTime.now()
        val localFile = copyDocumentToLocalStorage(
            sourceUri = fileUri,
            localId = localId,
            title = title
        )
        val actualFileSize = maxOf(fileSizeBytes, localFile.length())
        if (actualFileSize > MAX_DOCUMENT_SIZE_BYTES) {
            localFile.delete()
            throw IllegalArgumentException("El archivo supera el tamaño permitido.")
        }

        dao.upsertDocument(
            CachedDocumentEntity(
                id = localId,
                title = title.ifBlank { "Documento médico" },
                backendDocumentType = documentType,
                type = documentType.toDisplayDocumentType(),
                date = uploadDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault())),
                section = "RECIENTES",
                tone = documentType.toDocumentTone(),
                description = description,
                sortOrder = -System.currentTimeMillis(),
                localUri = localFile.absolutePath,
                storageBucket = "",
                storagePath = "",
                mimeType = mimeType,
                fileSizeBytes = actualFileSize,
                uploadedAt = uploadDate.format(API_DATE_TIME_FORMATTER),
                syncStatus = DOCUMENT_STATUS_UPLOADING,
                errorMessage = null
            )
        )

        uploadScope.launch {
            uploadCachedDocument(localId)
        }
    }

    fun retryPendingDocumentUploads() {
        uploadScope.launch {
            dao.getPendingDocuments().forEach { document ->
                if (!document.localUri.isNullOrBlank()) {
                    uploadCachedDocument(document.id)
                }
            }
        }
    }

    suspend fun syncAllCaches() {
        syncDocuments()
        syncDiaryNotes()
        syncNotifications()
    }

    suspend fun safeSyncDocuments(): Result<Unit> = runCatching {
        syncDocuments()
    }.mapError("No se pudieron sincronizar los documentos")

    suspend fun safeSyncDiaryNotes(): Result<Unit> = runCatching {
        syncDiaryNotes()
    }.mapError("No se pudo sincronizar el diario")

    suspend fun safeSyncNotifications(): Result<Unit> = runCatching {
        syncNotifications()
    }.mapError("No se pudieron sincronizar las notificaciones")

    private suspend fun ensureMedicalDocumentId(): Long {
        sessionManager.medicalDocumentId?.let { return it }

        val patientId = sessionManager.requireActivePatientId()
        val existingDocuments = api.getDocumentsByPatient(patientId)
        val existingId = existingDocuments.firstOrNull()?.id
        if (existingId != null) {
            sessionManager.saveMedicalDocumentId(existingId)
            return existingId
        }

        val createdDocument = api.createMedicalDocument(
            pe.edu.upc.careconnect.data.remote.CreateMedicalDocumentRequestDto(
                patientId = patientId
            )
        )

        sessionManager.saveMedicalDocumentId(createdDocument.id)
        return createdDocument.id
    }

    private fun <T> Result<T>.mapError(defaultMessage: String): Result<T> {
        return exceptionOrNull()?.let { throwable ->
            Result.failure(IllegalStateException(throwable.toUserMessage(defaultMessage), throwable))
        } ?: this
    }

    private fun copyDocumentToLocalStorage(sourceUri: Uri, localId: String, title: String): File {
        val targetDirectory = File(context.filesDir, "pending_documents").apply { mkdirs() }
        val targetFile = File(targetDirectory, "$localId-${title.sanitizeFileName()}")

        context.contentResolver.openInputStream(sourceUri)?.use { input ->
            targetFile.outputStream().use { output ->
                input.copyTo(output)
            }
        } ?: throw IllegalArgumentException("No se pudo abrir el archivo seleccionado.")

        return targetFile
    }

    private suspend fun uploadCachedDocument(documentId: String) {
        val document = dao.getDocumentById(documentId) ?: return
        val localPath = document.localUri
        val localFile = localPath?.let { File(it) }

        if (localFile == null || !localFile.exists()) {
            dao.upsertDocument(
                document.copy(
                    syncStatus = DOCUMENT_STATUS_ERROR,
                    errorMessage = "No se encontró el archivo local para subirlo."
                )
            )
            return
        }

        dao.upsertDocument(
            document.copy(
                syncStatus = DOCUMENT_STATUS_UPLOADING,
                errorMessage = null
            )
        )

        runCatching {
            val medicalDocumentId = ensureMedicalDocumentId()
            val mediaType = document.mimeType.ifBlank { "application/octet-stream" }.toMediaTypeOrNull()
            val filePart = MultipartBody.Part.createFormData(
                name = "file",
                filename = document.title,
                body = localFile.asRequestBody(mediaType)
            )

            api.uploadDocumentFile(
                medicalDocumentId = medicalDocumentId,
                file = filePart,
                documentType = document.backendDocumentType.toPlainRequestBody(),
                title = document.title.toPlainRequestBody(),
                description = document.description.takeIf { it.isNotBlank() }?.toPlainRequestBody(),
                uploadedAt = document.uploadedAt?.toPlainRequestBody()
            )
        }.onSuccess { medicalDocument ->
            val uploadedItem = medicalDocument.documentItems.maxByOrNull { item -> item.id }
            if (uploadedItem == null) {
                dao.upsertDocument(
                    document.copy(
                        syncStatus = DOCUMENT_STATUS_ERROR,
                        errorMessage = "No se pudo confirmar la subida del documento."
                    )
                )
                return
            }

            dao.upsertDocument(uploadedItem.toCachedDocument(section = "RECIENTES"))
            dao.deleteDocument(document.id)
            localFile.delete()
        }.onFailure { throwable ->
            dao.upsertDocument(
                document.copy(
                    syncStatus = DOCUMENT_STATUS_ERROR,
                    errorMessage = throwable.toUserMessage("No se pudo subir el documento. Inténtalo nuevamente.")
                )
            )
        }
    }

    private fun DocumentItemDto.toCachedDocument(section: String): CachedDocumentEntity {
        return CachedDocumentEntity(
            id = id.toString(),
            title = title,
            backendDocumentType = documentType,
            type = documentType.toDisplayDocumentType(),
            date = uploadedAt.toReadableDate(),
            section = section,
            tone = documentType.toDocumentTone(),
            description = description.orEmpty(),
            sortOrder = -id,
            localUri = null,
            storageBucket = storageBucket.orEmpty(),
            storagePath = storagePath.orEmpty(),
            mimeType = mimeType,
            fileSizeBytes = fileSizeBytes,
            uploadedAt = uploadedAt,
            syncStatus = syncStatus ?: DOCUMENT_STATUS_SYNCED,
            errorMessage = null
        )
    }

    companion object {
        @Volatile
        private var instance: CareCacheRepository? = null

        fun getInstance(context: Context): CareCacheRepository {
            return instance ?: synchronized(this) {
                instance ?: CareCacheRepository(
                    context = context.applicationContext,
                    database = CareCacheDatabase.getInstance(context)
                ).also { repository ->
                    instance = repository
                }
            }
        }
    }
}

private val API_DATE_TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
private const val MAX_DOCUMENT_SIZE_BYTES: Long = 10L * 1024L * 1024L
private const val DOCUMENT_STATUS_PENDING = "PENDING"
private const val DOCUMENT_STATUS_UPLOADING = "UPLOADING"
private const val DOCUMENT_STATUS_SYNCED = "SYNCED"
private const val DOCUMENT_STATUS_ERROR = "ERROR"

private fun String.toPlainRequestBody() = toRequestBody("text/plain".toMediaTypeOrNull())

private fun String.sanitizeFileName(): String {
    return ifBlank { "documento-medico" }
        .replace(Regex("[^A-Za-z0-9._-]"), "-")
        .replace(Regex("-+"), "-")
}

private fun String?.toReadableDate(): String {
    if (this.isNullOrBlank()) {
        return "Sin fecha"
    }

    return runCatching {
        OffsetDateTime.parse(this).format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault()))
    }.recoverCatching {
        LocalDateTime.parse(this, API_DATE_TIME_FORMATTER)
            .format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault()))
    }.getOrDefault(this)
}

private fun String?.toReadableDateTime(): String {
    if (this.isNullOrBlank()) {
        return "Sin fecha"
    }

    return runCatching {
        OffsetDateTime.parse(this).format(DateTimeFormatter.ofPattern("dd MMM, hh:mm a", Locale.getDefault()))
    }.recoverCatching {
        LocalDateTime.parse(this, API_DATE_TIME_FORMATTER)
            .format(DateTimeFormatter.ofPattern("dd MMM, hh:mm a", Locale.getDefault()))
    }.getOrDefault(this)
}

private fun String.toDisplayDocumentType(): String {
    return when (this) {
        "PRESCRIPTION" -> "Receta"
        "LAB_RESULT" -> "Laboratorio"
        "CLINICAL_REPORT" -> "Informe"
        "IMAGING" -> "Imagen"
        "REFERRAL" -> "Referencia"
        "VACCINATION_RECORD" -> "Vacuna"
        "INSURANCE_FORM" -> "Seguro"
        else -> "Otro"
    }
}

private fun String.toDocumentTone(): String {
    return when (this) {
        "IMAGING" -> "orange"
        "PRESCRIPTION", "VACCINATION_RECORD" -> "green"
        else -> "purple"
    }
}

private fun String.toDisplayStatus(): String {
    return when (this) {
        "READ" -> "Leído"
        "SCHEDULED" -> "Pendiente"
        "SENT", "DELIVERED" -> "Enviado"
        "FAILED" -> "Fallido"
        "CANCELLED" -> "Cancelado"
        else -> this
    }
}

private fun String.toStatusTone(): String {
    return when (this) {
        "READ" -> "read"
        "FAILED", "CANCELLED" -> "error"
        "SCHEDULED" -> "warning"
        else -> "neutral"
    }
}

private fun String.toActionLabel(): String {
    return when (this) {
        "ALERT" -> "Resolver"
        "REMINDER" -> "Ver detalles"
        else -> "Abrir"
    }
}

private fun String.toIconTone(): String {
    return when (this) {
        "CRITICAL" -> "error"
        "HIGH" -> "purple"
        "MEDIUM" -> "green"
        else -> "primary"
    }
}

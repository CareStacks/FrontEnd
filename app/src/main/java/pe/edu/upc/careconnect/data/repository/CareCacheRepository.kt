package pe.edu.upc.careconnect.data.repository

import android.content.Context
import pe.edu.upc.careconnect.data.local.CachedDiaryNoteEntity
import pe.edu.upc.careconnect.data.local.CachedDocumentEntity
import pe.edu.upc.careconnect.data.local.CachedNotificationEntity
import pe.edu.upc.careconnect.data.local.CareCacheDatabase

class CareCacheRepository private constructor(
    private val database: CareCacheDatabase
) {
    private val dao = database.careCacheDao()

    val documents = dao.observeDocuments()
    val diaryNotes = dao.observeDiaryNotes()
    val notifications = dao.observeNotifications()

    suspend fun seedIfEmpty() {
        if (dao.countDocuments() == 0) {
            dao.upsertDocuments(FakeCareCacheData.documents)
        }

        if (dao.countDiaryNotes() == 0) {
            dao.upsertDiaryNotes(FakeCareCacheData.diaryNotes)
        }

        if (dao.countNotifications() == 0) {
            dao.upsertNotifications(FakeCareCacheData.notifications)
        }
    }

    suspend fun saveDocument(
        type: String,
        description: String,
        date: String
    ) {
        val timestamp = System.currentTimeMillis()
        dao.upsertDocument(
            CachedDocumentEntity(
                id = "doc-$timestamp",
                title = description.ifBlank { "Documento médico" },
                type = type.ifBlank { "PDF" },
                date = date.ifBlank { "Hoy" },
                section = "RECIENTES",
                tone = when (type) {
                    "Digital" -> "green"
                    "Imagen" -> "orange"
                    else -> "purple"
                },
                description = description,
                sortOrder = -timestamp
            )
        )
    }

    suspend fun saveDiaryNote(
        title: String,
        body: String
    ) {
        val timestamp = System.currentTimeMillis()
        val noteBody = buildString {
            if (title.isNotBlank()) {
                append(title.trim())
                append(". ")
            }
            append(body.ifBlank { "Nota registrada sin contenido adicional." })
        }

        dao.upsertDiaryNote(
            CachedDiaryNoteEntity(
                id = "note-$timestamp",
                author = "Elena Rodriguez",
                timestamp = "HOY",
                body = noteBody,
                tags = "Nueva nota",
                tone = "green",
                isHighlighted = true,
                sortOrder = -timestamp
            )
        )
    }

    companion object {
        @Volatile
        private var instance: CareCacheRepository? = null

        fun getInstance(context: Context): CareCacheRepository {
            return instance ?: synchronized(this) {
                instance ?: CareCacheRepository(
                    CareCacheDatabase.getInstance(context)
                ).also { repository ->
                    instance = repository
                }
            }
        }
    }
}

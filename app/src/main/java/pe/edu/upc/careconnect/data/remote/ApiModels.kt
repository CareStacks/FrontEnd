package pe.edu.upc.careconnect.data.remote

data class LoginRequestDto(
    val email: String,
    val password: String
)

data class LoginResponseDto(
    val token: String,
    val type: String,
    val expiresIn: Long
)

data class RegisterUserRequestDto(
    val email: String,
    val password: String,
    val fullName: String,
    val role: String
)

data class UserDto(
    val id: String,
    val email: String,
    val fullName: String,
    val role: String,
    val active: Boolean,
    val createdAt: String?,
    val updatedAt: String?
)

data class CreateDiaryEntryRequestDto(
    val patientId: String,
    val content: String
)

data class DiaryEntryDto(
    val id: Long,
    val patientId: String,
    val content: String,
    val entryDate: String?
)

data class CreateMedicalDocumentRequestDto(
    val patientId: String
)

data class UploadDocumentItemRequestDto(
    val documentType: String,
    val title: String,
    val description: String,
    val fileUrl: String,
    val storageBucket: String? = null,
    val storagePath: String? = null,
    val mimeType: String,
    val fileSizeBytes: Long,
    val uploadedAt: String?,
    val syncStatus: String? = null
)

data class MedicalDocumentDto(
    val id: Long,
    val patientId: String,
    val documentItems: List<DocumentItemDto> = emptyList(),
    val createdAt: String?,
    val updatedAt: String?
)

data class DocumentItemDto(
    val id: Long,
    val documentType: String,
    val title: String,
    val description: String?,
    val fileUrl: String,
    val storageBucket: String? = null,
    val storagePath: String? = null,
    val mimeType: String,
    val fileSizeBytes: Long,
    val uploadedAt: String?,
    val syncStatus: String? = null
)

data class NotificationDto(
    val id: String,
    val recipientId: String,
    val healthEventId: String?,
    val title: String,
    val message: String,
    val type: String,
    val priority: String,
    val status: String,
    val deliveryChannel: String,
    val scheduledAt: String?,
    val sentAt: String?,
    val readAt: String?,
    val createdAt: String?,
    val updatedAt: String?
)

data class CreateHealthEventRequestDto(
    val patientId: String,
    val caregiverId: String?,
    val title: String,
    val description: String,
    val type: String,
    val startAt: String,
    val endAt: String
)

data class UpdateHealthEventRequestDto(
    val title: String,
    val description: String,
    val type: String,
    val startAt: String,
    val endAt: String
)

data class RescheduleHealthEventRequestDto(
    val startAt: String,
    val endAt: String
)

data class HealthEventDto(
    val id: String,
    val patientId: String,
    val caregiverId: String?,
    val title: String,
    val description: String?,
    val type: String,
    val status: String,
    val startAt: String?,
    val endAt: String?,
    val reminderAt: String?,
    val createdAt: String?,
    val updatedAt: String?
)

data class GrantConsentRequestDto(
    val caregiverId: String? = null,
    val caregiverEmail: String? = null,
    val allowedViews: List<String>
)

data class ProfileShareConsentDto(
    val id: String,
    val patientId: String,
    val patientFullName: String?,
    val caregiverId: String,
    val caregiverFullName: String?,
    val allowedViews: List<String>,
    val createdAt: String?,
    val updatedAt: String?
)

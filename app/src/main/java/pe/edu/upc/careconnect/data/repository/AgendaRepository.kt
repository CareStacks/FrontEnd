package pe.edu.upc.careconnect.data.repository

import android.content.Context
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import pe.edu.upc.careconnect.data.remote.ApiClient
import pe.edu.upc.careconnect.data.remote.CreateHealthEventRequestDto
import pe.edu.upc.careconnect.data.remote.HealthEventDto
import pe.edu.upc.careconnect.data.session.SessionManager

class AgendaRepository private constructor(context: Context) {
    private val sessionManager = SessionManager.getInstance(context)
    private val api = ApiClient.service

    suspend fun getAgendaEvents(): List<HealthEventDto> {
        val patientId = sessionManager.requireActivePatientId()
        return api.getAgendaEventsByPatient(patientId)
    }

    suspend fun getAgendaEventById(eventId: String): HealthEventDto {
        return api.getAgendaEventById(eventId)
    }

    suspend fun createAgendaEvent(
        title: String,
        description: String,
        type: String,
        startAt: LocalDateTime,
        endAt: LocalDateTime
    ) {
        val patientId = sessionManager.requireActivePatientId()
        val currentUserId = sessionManager.requireCurrentUserId()
        val currentUserRole = sessionManager.userRole
        api.createAgendaEvent(
            CreateHealthEventRequestDto(
                patientId = patientId,
                caregiverId = if (currentUserRole == "CAREGIVER") currentUserId else null,
                title = title,
                description = description,
                type = type,
                startAt = startAt.format(API_DATE_TIME_FORMATTER),
                endAt = endAt.format(API_DATE_TIME_FORMATTER)
            )
        )
    }

    suspend fun confirmAgendaEvent(eventId: String): HealthEventDto {
        return api.confirmAgendaEvent(eventId)
    }

    suspend fun rescheduleAgendaEvent(
        eventId: String,
        startAt: LocalDateTime,
        endAt: LocalDateTime
    ): HealthEventDto {
        return api.rescheduleAgendaEvent(
            eventId = eventId,
            request = pe.edu.upc.careconnect.data.remote.RescheduleHealthEventRequestDto(
                startAt = startAt.format(API_DATE_TIME_FORMATTER),
                endAt = endAt.format(API_DATE_TIME_FORMATTER)
            )
        )
    }

    suspend fun cancelAgendaEvent(eventId: String): HealthEventDto {
        return api.cancelAgendaEvent(eventId)
    }

    companion object {
        private val API_DATE_TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

        @Volatile
        private var instance: AgendaRepository? = null

        fun getInstance(context: Context): AgendaRepository {
            return instance ?: synchronized(this) {
                instance ?: AgendaRepository(context.applicationContext).also { repository ->
                    instance = repository
                }
            }
        }

        fun formatEventDate(rawValue: String?): String {
            if (rawValue.isNullOrBlank()) {
                return "Sin fecha"
            }

            return runCatching {
                LocalDateTime.parse(rawValue, API_DATE_TIME_FORMATTER)
                    .format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault()))
            }.getOrDefault(rawValue)
        }

        fun formatEventTimeRange(startAt: String?, endAt: String?): String {
            if (startAt.isNullOrBlank()) {
                return "Hora pendiente"
            }

            val formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault())
            val startText = runCatching {
                LocalDateTime.parse(startAt, API_DATE_TIME_FORMATTER).format(formatter)
            }.getOrDefault(startAt)

            val endText = endAt?.let {
                runCatching {
                    LocalDateTime.parse(it, API_DATE_TIME_FORMATTER).format(formatter)
                }.getOrDefault(it)
            }

            return if (endText.isNullOrBlank()) startText else "$startText - $endText"
        }
    }
}

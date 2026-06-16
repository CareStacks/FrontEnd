package pe.edu.upc.careconnect.data.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CareConnectApiService {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequestDto): LoginResponseDto

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterUserRequestDto): UserDto

    @GET("api/auth/me")
    suspend fun getCurrentUser(@Header("Authorization") authorization: String): UserDto

    @GET("api/diary/patient/{patientId}")
    suspend fun getDiaryEntriesByPatient(@Path("patientId") patientId: String): List<DiaryEntryDto>

    @POST("api/diary")
    suspend fun createDiaryEntry(@Body request: CreateDiaryEntryRequestDto): DiaryEntryDto

    @GET("api/documents/patient/{patientId}")
    suspend fun getDocumentsByPatient(@Path("patientId") patientId: String): List<MedicalDocumentDto>

    @POST("api/documents")
    suspend fun createMedicalDocument(@Body request: CreateMedicalDocumentRequestDto): MedicalDocumentDto

    @POST("api/documents/{medicalDocumentId}/items")
    suspend fun uploadDocumentItem(
        @Path("medicalDocumentId") medicalDocumentId: Long,
        @Body request: UploadDocumentItemRequestDto
    ): MedicalDocumentDto

    @GET("api/notifications/recipient/{recipientId}")
    suspend fun getNotificationsByRecipient(@Path("recipientId") recipientId: String): List<NotificationDto>

    @GET("api/agenda/patient/{patientId}")
    suspend fun getAgendaEventsByPatient(@Path("patientId") patientId: String): List<HealthEventDto>

    @GET("api/agenda/{id}")
    suspend fun getAgendaEventById(@Path("id") eventId: String): HealthEventDto

    @POST("api/agenda")
    suspend fun createAgendaEvent(@Body request: CreateHealthEventRequestDto): HealthEventDto

    @PUT("api/agenda/{id}")
    suspend fun updateAgendaEvent(
        @Path("id") eventId: String,
        @Body request: UpdateHealthEventRequestDto
    ): HealthEventDto

    @PATCH("api/agenda/{id}/confirm")
    suspend fun confirmAgendaEvent(@Path("id") eventId: String): HealthEventDto

    @PATCH("api/agenda/{id}/reschedule")
    suspend fun rescheduleAgendaEvent(
        @Path("id") eventId: String,
        @Body request: RescheduleHealthEventRequestDto
    ): HealthEventDto

    @PATCH("api/agenda/{id}/cancel")
    suspend fun cancelAgendaEvent(@Path("id") eventId: String): HealthEventDto

    @DELETE("api/agenda/{id}")
    suspend fun deleteAgendaEvent(@Path("id") eventId: String)
}

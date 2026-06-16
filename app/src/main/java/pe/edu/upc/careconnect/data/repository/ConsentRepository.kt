package pe.edu.upc.careconnect.data.repository

import android.content.Context
import pe.edu.upc.careconnect.data.remote.ApiClient
import pe.edu.upc.careconnect.data.remote.GrantConsentRequestDto
import pe.edu.upc.careconnect.data.remote.ProfileShareConsentDto
import pe.edu.upc.careconnect.data.session.SessionManager

class ConsentRepository private constructor(context: Context) {
    private val sessionManager = SessionManager.getInstance(context)
    private val api = ApiClient.service

    suspend fun shareProfileWithCaregiver(
        caregiverEmail: String,
        allowedViews: List<String>
    ): ProfileShareConsentDto {
        val token = requireNotNull(sessionManager.token) {
            "No hay una sesión activa. Iniciá sesión nuevamente."
        }

        return api.grantConsent(
            authorization = "Bearer $token",
            request = GrantConsentRequestDto(
                caregiverEmail = caregiverEmail.trim(),
                allowedViews = allowedViews.distinct()
            )
        )
    }

    companion object {
        @Volatile
        private var instance: ConsentRepository? = null

        fun getInstance(context: Context): ConsentRepository {
            return instance ?: synchronized(this) {
                instance ?: ConsentRepository(context.applicationContext).also { repository ->
                    instance = repository
                }
            }
        }
    }
}

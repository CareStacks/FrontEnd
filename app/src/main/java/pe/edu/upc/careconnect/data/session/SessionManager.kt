package pe.edu.upc.careconnect.data.session

import android.content.Context
import android.content.SharedPreferences
import pe.edu.upc.careconnect.data.remote.UserDto

class SessionManager private constructor(context: Context) {
    private val preferences: SharedPreferences = context.applicationContext.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )

    val token: String?
        get() = preferences.getString(KEY_TOKEN, null)

    val userId: String?
        get() = preferences.getString(KEY_USER_ID, null)

    val userFullName: String?
        get() = preferences.getString(KEY_USER_FULL_NAME, null)

    val userRole: String?
        get() = preferences.getString(KEY_USER_ROLE, null)

    val activePatientId: String?
        get() = preferences.getString(KEY_ACTIVE_PATIENT_ID, null)

    val medicalDocumentId: Long?
        get() = if (preferences.contains(KEY_MEDICAL_DOCUMENT_ID)) {
            preferences.getLong(KEY_MEDICAL_DOCUMENT_ID, -1L).takeIf { it >= 0L }
        } else {
            null
        }

    fun saveSession(token: String, user: UserDto) {
        val preservedActivePatientId = if (user.role == ROLE_CAREGIVER && user.id == userId) {
            activePatientId
        } else {
            null
        }

        preferences.edit()
            .putString(KEY_TOKEN, token)
            .putString(KEY_USER_ID, user.id)
            .putString(KEY_USER_FULL_NAME, user.fullName)
            .putString(KEY_USER_ROLE, user.role)
            .putString(
                KEY_ACTIVE_PATIENT_ID,
                if (user.role == ROLE_PATIENT) user.id else preservedActivePatientId
            )
            .remove(KEY_MEDICAL_DOCUMENT_ID)
            .apply()
    }

    fun saveActivePatientId(patientId: String) {
        preferences.edit()
            .putString(KEY_ACTIVE_PATIENT_ID, patientId)
            .remove(KEY_MEDICAL_DOCUMENT_ID)
            .apply()
    }

    fun clearActivePatientId() {
        preferences.edit()
            .remove(KEY_ACTIVE_PATIENT_ID)
            .remove(KEY_MEDICAL_DOCUMENT_ID)
            .apply()
    }

    fun requireCurrentUserId(): String {
        return requireNotNull(userId) {
            "No hay una sesión activa. Iniciá sesión nuevamente."
        }
    }

    fun requireActivePatientId(): String {
        val currentUserId = requireCurrentUserId()
        return when (userRole) {
            ROLE_PATIENT -> currentUserId
            ROLE_CAREGIVER -> requireNotNull(activePatientId) {
                "Tu cuenta es CAREGIVER, pero el backend actual no expone pacientes vinculados. Configurá un paciente activo desde tu perfil para continuar."
            }
            else -> currentUserId
        }
    }

    fun saveMedicalDocumentId(documentId: Long) {
        preferences.edit()
            .putLong(KEY_MEDICAL_DOCUMENT_ID, documentId)
            .apply()
    }

    fun clearMedicalDocumentId() {
        preferences.edit().remove(KEY_MEDICAL_DOCUMENT_ID).apply()
    }

    companion object {
        private const val PREFERENCES_NAME = "care_connect_session"
        private const val KEY_TOKEN = "token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_FULL_NAME = "user_full_name"
        private const val KEY_USER_ROLE = "user_role"
        private const val KEY_ACTIVE_PATIENT_ID = "active_patient_id"
        private const val KEY_MEDICAL_DOCUMENT_ID = "medical_document_id"
        private const val ROLE_PATIENT = "PATIENT"
        private const val ROLE_CAREGIVER = "CAREGIVER"

        @Volatile
        private var instance: SessionManager? = null

        fun getInstance(context: Context): SessionManager {
            return instance ?: synchronized(this) {
                instance ?: SessionManager(context).also { manager ->
                    instance = manager
                }
            }
        }
    }
}

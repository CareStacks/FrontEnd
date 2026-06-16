package pe.edu.upc.careconnect.data.repository

import android.content.Context
import java.net.SocketTimeoutException
import pe.edu.upc.careconnect.data.remote.ApiClient
import pe.edu.upc.careconnect.data.remote.LoginRequestDto
import pe.edu.upc.careconnect.data.remote.RegisterUserRequestDto
import pe.edu.upc.careconnect.data.session.SessionManager

class AuthRepository private constructor(context: Context) {
    private val sessionManager = SessionManager.getInstance(context)
    private val api = ApiClient.service

    suspend fun login(email: String, password: String) {
        val loginResponse = api.login(
            LoginRequestDto(
                email = email,
                password = password
            )
        )

        val currentUser = api.getCurrentUser("Bearer ${loginResponse.token}")
        sessionManager.saveSession(loginResponse.token, currentUser)
    }

    suspend fun register(fullName: String, email: String, password: String, role: String) {
        runCatching {
            api.register(
                RegisterUserRequestDto(
                    email = email,
                    password = password,
                    fullName = fullName,
                    role = role
                )
            )
        }.onFailure { throwable ->
            if (throwable is SocketTimeoutException) {
                runCatching {
                    login(email = email, password = password)
                }.getOrElse {
                    throw throwable
                }
                return
            }

            throw throwable
        }

        login(email = email, password = password)
    }

    fun currentUserName(): String? = sessionManager.userFullName

    companion object {
        @Volatile
        private var instance: AuthRepository? = null

        fun getInstance(context: Context): AuthRepository {
            return instance ?: synchronized(this) {
                instance ?: AuthRepository(context.applicationContext).also { repository ->
                    instance = repository
                }
            }
        }
    }
}

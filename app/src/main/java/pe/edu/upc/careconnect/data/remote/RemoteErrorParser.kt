package pe.edu.upc.careconnect.data.remote

import java.net.SocketTimeoutException
import java.net.UnknownHostException
import retrofit2.HttpException

fun Throwable.toUserMessage(defaultMessage: String): String {
    if (this is SocketTimeoutException) {
        return "El servidor tardó demasiado en responder. Si el backend estaba dormido en Render, intentá nuevamente en unos segundos."
    }

    if (this is UnknownHostException) {
        return "No se pudo conectar con el servidor. Verificá tu internet o la URL del backend."
    }

    if (this is HttpException) {
        return try {
            val body = response()?.errorBody()?.string()?.trim()
            if (body.isNullOrBlank()) {
                "$defaultMessage (HTTP ${code()})"
            } else {
                "$defaultMessage (HTTP ${code()})"
            }
        } catch (_: Exception) {
            "$defaultMessage (HTTP ${code()})"
        }
    }

    return message ?: defaultMessage
}

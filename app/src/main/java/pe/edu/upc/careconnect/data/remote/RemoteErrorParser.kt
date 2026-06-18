package pe.edu.upc.careconnect.data.remote

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import retrofit2.HttpException

fun Throwable.toUserMessage(defaultMessage: String): String {
    if (this is SocketTimeoutException) {
        return "El servidor tardó demasiado en responder. Inténtalo nuevamente en unos segundos."
    }

    if (this is UnknownHostException) {
        return "No se pudo conectar con el servidor. Verifica tu conexión a internet e inténtalo nuevamente."
    }

    if (this is HttpException) {
        val parsedMessage = runCatching {
            response()?.errorBody()?.string()?.toFriendlyApiErrorMessage()
        }.getOrNull()

        if (!parsedMessage.isNullOrBlank()) {
            return "${parsedMessage} (HTTP ${code()})"
        }

        return friendlyHttpMessage(code(), defaultMessage)
    }

    return message
        ?.trim()
        ?.takeUnless { it.isTechnicalOnlyMessage() }
        ?: defaultMessage
}

private fun friendlyHttpMessage(code: Int, defaultMessage: String): String {
    val message = when (code) {
        400 -> "Los datos ingresados no son válidos. Revisa la información e inténtalo nuevamente."
        401 -> if (defaultMessage.contains("sesión", ignoreCase = true)) {
            "Tu sesión expiró o no está activa. Inicia sesión nuevamente."
        } else {
            "No pudimos iniciar sesión. Revisa tu correo y contraseña."
        }
        403 -> "No tienes permisos para realizar esta acción."
        404 -> "No se encontró la información solicitada."
        408 -> "La solicitud tardó demasiado. Inténtalo nuevamente."
        409 -> "No se pudo completar la acción porque la información ya fue modificada."
        422 -> "Falta completar un campo obligatorio o algún dato no es válido."
        in 500..599 -> "No se pudo completar la acción. Inténtalo nuevamente."
        else -> defaultMessage
    }

    return "$message (HTTP $code)"
}

private fun String.isTechnicalOnlyMessage(): Boolean {
    val normalized = trim()
    return normalized.matches(Regex("^\\d{3}$")) ||
        normalized.matches(Regex("^HTTP\\s+\\d{3}.*$", RegexOption.IGNORE_CASE)) ||
        normalized.equals("null", ignoreCase = true)
}

private fun String.toFriendlyApiErrorMessage(): String? {
    if (isBlank()) return null

    val root = runCatching { JsonParser.parseString(this).asJsonObject }.getOrNull()
        ?: return toKnownFriendlyText()

    root.getAsJsonObjectOrNull("errors")?.let { errors ->
        val fieldMessages = errors.entrySet()
            .mapNotNull { entry -> entry.value.asStringOrNull()?.toFriendlyFieldError(entry.key) }
            .distinct()

        if (fieldMessages.isNotEmpty()) {
            return fieldMessages.joinToString(separator = "\n")
        }
    }

    root.getStringOrNull("detail")?.toKnownFriendlyText()?.let { return it }
    root.getStringOrNull("message")?.toKnownFriendlyText()?.let { return it }
    root.getStringOrNull("error")?.toKnownFriendlyText()?.let { return it }

    return null
}

private fun JsonObject.getAsJsonObjectOrNull(memberName: String): JsonObject? {
    val element = get(memberName) ?: return null
    return if (element.isJsonObject) element.asJsonObject else null
}

private fun JsonObject.getStringOrNull(memberName: String): String? {
    return get(memberName)?.asStringOrNull()
}

private fun com.google.gson.JsonElement.asStringOrNull(): String? {
    return when {
        isJsonPrimitive && asJsonPrimitive.isString -> asString
        isJsonPrimitive -> asString
        isJsonArray && asJsonArray.size() > 0 -> asJsonArray.first().asStringOrNull()
        else -> null
    }
}

private fun String.toFriendlyFieldError(fieldName: String): String {
    return when (fieldName) {
        "email" -> when {
            contains("required", ignoreCase = true) || contains("blank", ignoreCase = true) ->
                "Ingresa tu correo electrónico."
            contains("format", ignoreCase = true) || contains("valid", ignoreCase = true) ->
                "Ingresa un correo electrónico válido."
            else -> "Revisa el correo electrónico ingresado."
        }
        "password" -> when {
            contains("required", ignoreCase = true) || contains("blank", ignoreCase = true) ->
                "Ingresa tu contraseña."
            contains("8", ignoreCase = true) || contains("characters", ignoreCase = true) ->
                "La contraseña debe tener al menos 8 caracteres."
            contains("uppercase", ignoreCase = true) || contains("number", ignoreCase = true) ->
                "La contraseña debe incluir al menos una mayúscula y un número."
            else -> "Revisa la contraseña ingresada."
        }
        "fullName" -> when {
            contains("required", ignoreCase = true) || contains("blank", ignoreCase = true) ->
                "Ingresa tu nombre completo."
            contains("150", ignoreCase = true) || contains("exceed", ignoreCase = true) ->
                "El nombre completo no debe superar 150 caracteres."
            else -> "Revisa el nombre completo ingresado."
        }
        "role" -> "Selecciona si eres paciente o cuidador."
        "title" -> "Ingresa el nombre del documento."
        "documentType" -> "Selecciona el tipo de documento."
        "mimeType" -> "El tipo de archivo no es válido. Usa PDF, JPG o PNG."
        "fileSizeBytes" -> "El archivo supera el tamaño permitido."
        "fileUrl", "file" -> "Selecciona un archivo antes de continuar."
        else -> toKnownFriendlyText() ?: "Revisa el campo $fieldName."
    }
}

private fun String.toKnownFriendlyText(): String? {
    val value = trim()
    if (value.isBlank() || value.isTechnicalOnlyMessage()) return null

    return when {
        value.contains("Bad credentials", ignoreCase = true) ->
            "Correo o contraseña incorrectos."
        value.contains("Email is required", ignoreCase = true) ->
            "Ingresa tu correo electrónico."
        value.contains("Invalid email", ignoreCase = true) ->
            "Ingresa un correo electrónico válido."
        value.contains("Password is required", ignoreCase = true) ->
            "Ingresa tu contraseña."
        value.contains("Password must be at least", ignoreCase = true) ->
            "La contraseña debe tener al menos 8 caracteres."
        value.contains("uppercase", ignoreCase = true) || value.contains("one number", ignoreCase = true) ->
            "La contraseña debe incluir al menos una mayúscula y un número."
        value.contains("Full name is required", ignoreCase = true) ->
            "Ingresa tu nombre completo."
        value.contains("Full name must not exceed", ignoreCase = true) ->
            "El nombre completo no debe superar 150 caracteres."
        value.contains("Only PDF", ignoreCase = true) ->
            "El tipo de archivo no es válido. Usa PDF, JPG o PNG."
        value.contains("File size", ignoreCase = true) || value.contains("10 MB", ignoreCase = true) ->
            "El archivo supera el tamaño permitido."
        value.contains("storage", ignoreCase = true) || value.contains("almacenamiento", ignoreCase = true) ->
            "No se pudo conectar con el almacenamiento."
        else -> value.takeIf { !it.isTechnicalOnlyMessage() }
    }
}

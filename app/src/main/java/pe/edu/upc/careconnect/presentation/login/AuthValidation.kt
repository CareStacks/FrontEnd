package pe.edu.upc.careconnect.presentation.login

internal fun String.isValidEmailAddress(): Boolean {
    return trim().matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))
}

internal fun String.isValidFullName(): Boolean {
    val normalized = trim().replace(Regex("\\s+"), " ")
    return normalized.length >= 3 && normalized.any { it.isLetter() }
}

internal fun String.hasRequiredPasswordStrength(): Boolean {
    return length >= 8 && any { it.isUpperCase() } && any { it.isDigit() }
}

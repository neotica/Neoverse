package id.neotica.auth.verify

data class VerifyEmailState(
    val email: String = "",
    val isResending: Boolean = false,
    val toastMessage: String? = null,
) {
    val canResend: Boolean get() = email.isNotBlank() && !isResending
}

sealed interface VerifyEmailEvent {
    data object LoggedOut : VerifyEmailEvent
}

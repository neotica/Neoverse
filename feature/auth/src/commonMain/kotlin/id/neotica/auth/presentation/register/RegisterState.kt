package id.neotica.auth.presentation.register

data class RegisterState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val agreedToTerms: Boolean = false,
    val isPasswordVisible: Boolean = false,
    val usernameError: String? = null,
    val isLoading: Boolean = false,
    val toastMessage: String? = null,
) {
    val canSubmit: Boolean
        get() = username.isNotBlank() &&
            username.length >= 3 &&
            !username.contains(" ") &&
            email.isNotBlank() &&
            password.isNotBlank() &&
            agreedToTerms &&
            usernameError == null &&
            !isLoading
}

sealed interface RegisterEvent {
    data class Registered(val email: String) : RegisterEvent
}

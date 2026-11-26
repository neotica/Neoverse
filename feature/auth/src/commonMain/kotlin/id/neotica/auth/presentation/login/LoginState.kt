package id.neotica.auth.presentation.login

data class LoginState(
    val username: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val toastMessage: String? = null,
) {
    val canSubmit: Boolean get() = username.isNotBlank() && password.isNotEmpty() && !isLoading
}

sealed interface LoginEvent {
    data object Success : LoginEvent
}

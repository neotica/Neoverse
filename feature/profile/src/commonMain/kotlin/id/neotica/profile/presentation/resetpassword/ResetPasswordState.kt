package id.neotica.profile.presentation.resetpassword

data class ResetPasswordState(
    val email: String = "",
    val isLoading: Boolean = false,
    val toastMessage: String? = null,
) {
    val canSubmit: Boolean get() = email.isNotBlank() && !isLoading
}

package id.neotica.profile.presentation

data class ProfileState(
    val username: String = "",
    val showAccountSettings: Boolean = false,
    val showLogoutDialog: Boolean = false,
    val toastMessage: String? = null,
)

sealed interface ProfileEvent {
    data object LoggedOut : ProfileEvent
    data object RequireLogin : ProfileEvent
}

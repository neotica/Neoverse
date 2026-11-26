package id.neotica.neoverse.presentation.screen

data class SplashState(
    val isLoading: Boolean = true,
)

sealed interface SplashEvent {
    data object NavigateHome : SplashEvent
    data object NavigateLogin : SplashEvent
}

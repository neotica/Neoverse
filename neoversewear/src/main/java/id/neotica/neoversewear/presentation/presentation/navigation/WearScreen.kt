package id.neotica.neoversewear.presentation.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class WearScreen {
    @Serializable
    data object HomeNavScreen: WearScreen()
    @Serializable
    data object HomeScreen: WearScreen()

    // Auth
    @Serializable
    data object LoginScreen: WearScreen()
}
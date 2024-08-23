package id.neotica.neoverse.presentation.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable
    object Splash

    // Home Navigation
    @Serializable
    object HomeScreen

    @Serializable
    object Chat
    @Serializable
    object Setting
    @Serializable
    object ChatFromHome

    // Auth Navigation
    @Serializable
    object LoginScreen
    @Serializable
    object RegisterScreen
    @Serializable
    object ProfileScreen
}

@Serializable
sealed class RootScreen {

    @Serializable
    object AuthNav
    @Serializable
    object HomeNav
}

data class NavigationItem(
    val title: String,
    val icon: ImageVector,
    val screen: Any
)
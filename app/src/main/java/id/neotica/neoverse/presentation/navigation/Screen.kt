package id.neotica.neoverse.presentation.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable
    data object Splash: Screen()

    // Home Navigation
    @Serializable
    object HomeScreen

    @Serializable
    data class Chat(val chatId: String)
    @Serializable
    object Setting
    @Serializable
    object ChatFromHome

    // Auth Navigation
    @Serializable
    data object LoginScreen: Screen()
    @Serializable
    data object RegisterScreen: Screen()
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
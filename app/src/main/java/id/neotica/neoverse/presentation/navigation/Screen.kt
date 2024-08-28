package id.neotica.neoverse.presentation.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable
    data object Splash: Screen()

    // Home Navigation
    @Serializable
    data object HomeScreen: Screen()

    @Serializable
    data class Chat(val chatId: String)
    @Serializable
    data object Setting: Screen()
    @Serializable
    data object ChatFromHome: Screen()

    // Auth Navigation
    @Serializable
    data object LoginScreen: Screen()
    @Serializable
    data object RegisterScreen: Screen()
    @Serializable
    data object ProfileScreen: Screen()

    // Games Navigation
    @Serializable
    data object HomeGame: Screen()
}

@Serializable
sealed class RootScreen {

    @Serializable
    object AuthNav
    @Serializable
    object HomeNav
    @Serializable
    object GameNav
}

data class NavigationItem(
    val title: String,
    val icon: ImageVector,
    val screen: Any
)
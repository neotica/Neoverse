package id.neotica.neoverse.presentation.navigation

import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    object Splash: Screen("splashView")
    object Chat: Screen("chatView")
    object Setting: Screen("settingView")
    object ChatFromHome: Screen("chatFromHomeView")

    // Auth Navigation
    object Login: Screen("loginView")
    object Register: Screen("registerView")
}

sealed class RootScreen(val route: String) {
    object AuthNav: RootScreen("authNavigation")
    object HomeNav: RootScreen("homeNavigation")
}

data class NavigationItem(
    val title: String,
    val icon: ImageVector,
    val screen: String
)
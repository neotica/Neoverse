package id.neotica.neoverse.presentation.navigation

sealed class Screen(val route: String) {
    object Home: Screen("homeView")
    object Chat: Screen("chatView")
    object Setting: Screen("settingView")
    object ChatFromHome: Screen("chatFromHomeView")

    // Auth Navigation
    object Login: Screen("loginView")
    object Register: Screen("registerView")
}
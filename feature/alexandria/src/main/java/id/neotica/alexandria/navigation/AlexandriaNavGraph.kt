package id.neotica.alexandria.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import id.neotica.alexandria.screen.LibraryView
import id.neotica.alexandria.screen.bloodyage.BloodyAgeView
import id.neotica.alexandria.screen.goetia.GoetiaView
import id.neotica.ui.navigation.AlexandriaScreen
import id.neotica.ui.navigation.RootScreen

fun NavGraphBuilder.alexandriaNavGraph(navController: NavController) {
    navigation<RootScreen.AlexandriaNav>(
        startDestination = AlexandriaScreen.HomeScreen
    ) {
        composable<AlexandriaScreen.HomeScreen> {
            LibraryView(navController)
        }
        composable<AlexandriaScreen.GoetiaScreen> {
            GoetiaView(navController)
        }
        composable<AlexandriaScreen.BloodyAgeScreen> {
            BloodyAgeView(navController)
        }
    }
}
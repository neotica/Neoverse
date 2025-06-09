package id.neotica.alexandria.presentation.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import id.neotica.alexandria.navigation.alexandriaNavGraph
import id.neotica.ui.navigation.RootScreen
import id.neotica.ui.state.AppVariant
import org.koin.compose.getKoin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavGraph(
    navController: NavHostController,
    paddingValues: PaddingValues,
) {
    val appVariant: AppVariant = getKoin().get()

    NavHost(
        navController,
        startDestination = RootScreen.AlexandriaNav,
        enterTransition = { fadeIn(tween(100)) },
        popEnterTransition = { EnterTransition.None },
        exitTransition = { fadeOut(tween(100)) },
        popExitTransition = { ExitTransition.None }
    ) {
        alexandriaNavGraph(navController, appVariant)
        profileNavGraph(navController, paddingValues, appVariant)
    }
}
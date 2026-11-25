package id.neotica.neoverse.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import id.neotica.domain.session.SessionRepository
import id.neotica.routes.RootScreen
import id.neotica.routes.Screen
import id.neotica.ui.shared.theme.DarkBackground
import org.koin.compose.getKoin

@Composable
fun SplashView(navController: NavController) {
    val session: SessionRepository = getKoin().get()

    Box(
        modifier = Modifier
            .background(DarkBackground)
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {}

    LaunchedEffect(Unit) {
        if (session.isLoggedIn()) {
            navController.navigate(RootScreen.HomeNav) {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
            }
        } else {
            navController.navigate(Screen.LoginScreen) {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
            }
        }
    }
}

package id.neotica.neoverse.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import id.neotica.neoverse.presentation.navigation.RootScreen
import kotlinx.coroutines.delay

@Composable
fun SplashView(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize()
            .background(color = Color.Green),
        contentAlignment = Alignment.Center,
    ) {
        Column {
            Text("This is splash screen.")
            Text("💀\uD83D\uDC80\uD83D\uDC80")
        }
    }

    LaunchedEffect(Unit) {
        delay(200)
        navController.navigate(RootScreen.HomeNav) {
            popUpTo(navController.graph.startDestinationId) {
                inclusive = true
            }
        }
    }
}
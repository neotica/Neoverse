package id.neotica.neoverse.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import id.neotica.neoverse.R
import id.neotica.ui.navigation.RootScreen
import kotlinx.coroutines.delay

@Composable
fun SplashView(navController: NavController) {
    Box(
        modifier = Modifier
            .paint(
                painter = painterResource(id = R.drawable.splash_screen),
                contentScale = ContentScale.Crop
            )
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            modifier = Modifier
                .fillMaxSize(),
            painter = painterResource(
                id = R.drawable.splash_screen),
            contentScale = ContentScale.Crop,
            contentDescription = "")
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
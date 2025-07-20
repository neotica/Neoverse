package id.neotica.neoverse.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import id.neotica.routes.RootScreen
import id.neotica.routes.Screen
import id.neotica.ui.shared.theme.DarkBackground

@Composable
fun SplashView(navController: NavController) {
    Box(
        modifier = Modifier
            /*.paint(
                painter = painterResource(id = R.drawable.splash_screen),
                contentScale = ContentScale.Crop
            )*/
            .background(DarkBackground)
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        /*Image(
            modifier = Modifier
                .fillMaxSize(),
            painter = painterResource(
                id = R.drawable.splash_screen),
            contentScale = ContentScale.Crop,
            contentDescription = "")
        Column {
            Text("This is splash screen.")
            Text("💀\uD83D\uDC80\uD83D\uDC80")
        }*/
    }

    LaunchedEffect(Unit) {
        if (Firebase.auth.currentUser != null) {
            navController.navigate(RootScreen.HomeNav) {
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = true
                }
            }
        } else {
            navController.navigate(Screen.LoginScreen) {
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = true
                }
            }
        }
    }
}
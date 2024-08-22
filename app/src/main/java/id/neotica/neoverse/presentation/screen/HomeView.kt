package id.neotica.neoverse.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import id.neotica.neoverse.presentation.navigation.RootScreen

private lateinit var auth: FirebaseAuth

@Composable
fun HomeView(navController: NavController, paddingValues: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Blue),
        contentAlignment = Alignment.Center
    ) {
        auth = FirebaseAuth.getInstance()
        Column(modifier = Modifier.padding(paddingValues)) {
            Text("homeview")
            Button(onClick = {
                if (auth.currentUser != null) {
                    auth.signOut()
                } else {
                    navController.navigate(RootScreen.AuthNav.route)
                }
            }) {
                Text("To auth")
            }
        }
    }

}
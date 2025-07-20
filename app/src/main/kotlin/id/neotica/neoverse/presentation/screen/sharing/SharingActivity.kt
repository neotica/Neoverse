package id.neotica.neoverse.presentation.screen.sharing

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import id.neotica.ui.shared.theme.NeoverseTheme

class SharingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val sharedText = intent?.getStringExtra(Intent.EXTRA_TEXT)

        setContent {
            NeoverseTheme {
                val navController = rememberNavController()

                SharingNavGraph(navController, sharedText ?: "")
            }
        }
    }
}
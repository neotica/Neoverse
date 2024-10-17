package id.neotica.world.feat.world

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.NeoButton
import id.neotica.ui.navigation.RootScreen
import id.neotica.ui.navigation.Screen

@Composable
fun WorldView(
    navController: NavController,
    paddingValues: PaddingValues,
) {
    BasicScaffold(
        topBarTitle = "World",
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        NeoButton("To Casino") {
            navController.navigate(Screen.GameScreen)
        }
        NeoButton("Test") {
            navController.navigate(RootScreen.HomeNav)
        }
        NeoButton("To Kanye") {
            navController.navigate(Screen.KanyeScreen)
        }
        NeoButton("To Market") {
            navController.navigate(Screen.MarketScreen)
        }
    }
}
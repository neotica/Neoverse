package id.neotica.neoverse.presentation.screen.sharing.screen

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import id.neotica.home.addchat.AddChatView
import id.neotica.ui.components.BasicScaffold

@Composable
fun MainSharingScreen(navController: NavController) {
    BasicScaffold("Share to") {
        AddChatView(navController)
    }
}
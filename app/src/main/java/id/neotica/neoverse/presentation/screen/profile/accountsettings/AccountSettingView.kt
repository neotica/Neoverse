package id.neotica.neoverse.presentation.screen.profile.accountsettings

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.NeoButton
import id.neotica.ui.navigation.Screen

@Composable
fun AccountSettingView(
    navController: NavController,
    paddingValues: PaddingValues,
) {
    BasicScaffold(
        topBarTitle = "Account Settings",
        horizontalAlignment = Alignment.CenterHorizontally,
        paddingValues = paddingValues,
        navController = navController,
        enableActionBar = true
    ) {
        NeoButton("Change Password", true) {
        }
        NeoButton("Delete Account", true, negative = true) {
            navController.navigate(Screen.DeleteAccountScreen)
        }
    }
}
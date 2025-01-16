package id.neotica.neoverse.presentation.screen.profile.accountsettings

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.MenuItem
import id.neotica.ui.navigation.Screen
import id.neotica.ui.theme.NegativePrimary

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
        val context = LocalContext.current
        Column(
            Modifier.padding(16.dp)
        ) {
            MenuItem("Change Password") {
                Toast.makeText(context, "Please contact our support email to change password.", Toast.LENGTH_SHORT).show()
            }
            MenuItem(
                "Delete Account",
                textColor = NegativePrimary
            ) {
                navController.navigate(Screen.DeleteAccountScreen)
            }
        }

    }
}
package id.neotica.neoverse.presentation.screen.profile.privacypolicy

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import id.neotica.ui.components.BasicScaffold

@Composable
fun PrivacyPolicyView(navController: NavController, paddingValues: PaddingValues, modifier: Modifier = Modifier) {
    BasicScaffold(
        topBarTitle = "Privacy Policy",
        true,
        navController = navController,
        paddingValues = paddingValues
    ) { }
}
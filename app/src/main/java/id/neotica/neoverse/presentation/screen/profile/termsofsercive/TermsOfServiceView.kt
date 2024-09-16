package id.neotica.neoverse.presentation.screen.profile.termsofsercive

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import id.neotica.ui.components.BasicScaffold

@Composable
fun TermsOfServiceView(navController: NavController,  paddingValues: PaddingValues, modifier: Modifier = Modifier) {
    BasicScaffold(
        topBarTitle = "Terms of Service",
        true,
        navController = navController,
        paddingValues = paddingValues
    ) { }
}
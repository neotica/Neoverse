package id.neotica.profile.presentation.termsofservice

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalUriHandler
import androidx.navigation.NavController
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TermsOfServiceRoute(
    navController: NavController,
    viewModel: TermsOfServiceViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val uriHandler = LocalUriHandler.current

    TermsOfServiceView(
        state = state,
        onLoadingChanged = viewModel::onLoadingChanged,
        onBack = navController::popBackStack,
        onOpenExternal = { uriHandler.openUri(it) },
    )
}

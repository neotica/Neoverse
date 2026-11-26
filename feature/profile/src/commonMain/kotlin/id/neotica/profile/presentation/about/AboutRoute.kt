package id.neotica.profile.presentation.about

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AboutRoute(
    navController: NavController,
    viewModel: AboutViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    AboutView(state = state, onBack = navController::popBackStack)
}

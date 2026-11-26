package id.neotica.chat

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import id.neotica.routes.RootScreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ChatRoute(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: ChatViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                ChatEvent.ConversationDeleted -> navController.navigate(RootScreen.HomeNav) {
                    popUpTo(RootScreen.HomeNav) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }

    ChatView(
        state = state,
        paddingValues = paddingValues,
        onDraftChange = viewModel::onDraftChange,
        onSend = viewModel::sendMessage,
        onBack = navController::popBackStack,
        onDelete = viewModel::deleteConversation,
        onToastDismissed = viewModel::onToastDismissed,
    )
}

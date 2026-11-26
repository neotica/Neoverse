package id.neotica.home.addchat

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import id.neotica.routes.Screen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AddChatRoute(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: AddChatViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is AddChatEvent.ConversationCreated -> {
                    val conversation = event.conversation
                    navController.navigate(
                        Screen.Conversation(
                            conversationId = conversation.id,
                            peerUsername = conversation.peerUsername,
                        )
                    ) {
                        popUpTo(Screen.AddChatScreen) { inclusive = true }
                    }
                }
            }
        }
    }

    AddChatView(
        state = state,
        paddingValues = paddingValues,
        onUsernameChange = viewModel::onUsernameChange,
        onSubmit = viewModel::startChat,
        onBack = navController::popBackStack,
        onToastDismissed = viewModel::onToastDismissed,
    )
}

package id.neotica.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import id.neotica.routes.Screen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeRoute(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadConversations()
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                HomeEvent.RequireLogin -> navController.navigate(Screen.LoginScreen) {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }

    HomeView(
        state = state,
        paddingValues = paddingValues,
        onQueryChange = viewModel::onQueryChange,
        onRetry = viewModel::loadConversations,
        onConversationClick = { conversation ->
            navController.navigate(
                Screen.Conversation(
                    conversationId = conversation.id,
                    peerUsername = conversation.peerUsername,
                )
            )
        },
        onNewChat = { navController.navigate(Screen.AddChatScreen) },
    )
}

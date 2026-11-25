package id.neotica.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.icerock.moko.resources.compose.painterResource
import id.neotica.domain.ApiResult
import id.neotica.domain.model.chat.Conversation
import id.neotica.domain.utils.convertLongToTime
import id.neotica.res.MR
import id.neotica.routes.Screen
import id.neotica.ui.shared.components.BasicScaffold
import id.neotica.ui.shared.components.ChatListItem
import id.neotica.ui.shared.components.LoadingIndicator
import id.neotica.ui.shared.components.NeoFAB
import id.neotica.ui.shared.components.droidcore.NeoSearchBar
import id.neotica.ui.shared.theme.FontSize
import id.neotica.ui.shared.theme.TransparentText20
import org.koin.compose.viewmodel.koinViewModel

private const val DefaultAvatar =
    "https://www.pngarts.com/files/6/User-Avatar-in-Suit-PNG.png"

@Composable
fun HomeView(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val state by viewModel.conversations.collectAsState()
    val search = rememberSaveable { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadConversations()
    }

    LaunchedEffect(Unit) {
        if (!viewModel.isLoggedIn()) {
            navController.navigate(Screen.LoginScreen) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    BasicScaffold(
        topBarTitle = "Chats",
        navController = navController,
        paddingValues = paddingValues,
    ) {
        Box(Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxSize()) {
                Text(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp),
                    text = "Welcome, ${viewModel.username.ifBlank { "there" }}!",
                    fontSize = FontSize.Big,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    text = "Pick a conversation or start a new one.",
                    fontSize = FontSize.Small,
                    color = TransparentText20,
                )
                NeoSearchBar(
                    value = search,
                    label = "Search chats",
                    padding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                )

                when (val result = state) {
                    is ApiResult.Loading -> LoadingIndicator()
                    is ApiResult.Error -> Text(
                        modifier = Modifier.padding(16.dp),
                        text = result.errorMessage ?: "Unable to load chats",
                        color = TransparentText20,
                    )
                    is ApiResult.Success -> {
                        val conversations = result.data.orEmpty().filter { conversation ->
                            search.value.isBlank() ||
                                conversation.peerUsername.contains(search.value, ignoreCase = true) ||
                                conversation.lastMessage.contains(search.value, ignoreCase = true)
                        }
                        if (conversations.isEmpty()) {
                            Text(
                                modifier = Modifier.padding(16.dp),
                                text = "No conversations yet. Start a new chat.",
                                color = TransparentText20,
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(bottom = 96.dp),
                            ) {
                                items(conversations, key = { it.id }) { conversation ->
                                    ConversationRow(conversation) {
                                        navController.navigate(
                                            Screen.Conversation(
                                                conversationId = conversation.id,
                                                peerUsername = conversation.peerUsername,
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            NeoFAB(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                onClick = { navController.navigate(Screen.AddChatScreen) },
            ) {
                Row(Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(MR.images.add_chat),
                        contentDescription = "New chat",
                    )
                    Text(text = "New chat", modifier = Modifier.padding(start = 8.dp))
                }
            }
        }
    }
}

@Composable
private fun ConversationRow(
    conversation: Conversation,
    onClick: () -> Unit,
) {
    ChatListItem(
        image = conversation.peerAvatarUrl ?: DefaultAvatar,
        onClick = onClick,
        content = {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = conversation.peerUsername.ifBlank { "Conversation" },
                fontWeight = FontWeight.Bold,
                fontSize = FontSize.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (conversation.updatedAt > 0) {
                Text(
                    text = convertLongToTime(conversation.updatedAt, true),
                    fontSize = FontSize.Smaller,
                    color = TransparentText20,
                    maxLines = 1,
                )
            }
        }
        Text(
            text = conversation.lastMessage.ifBlank { "No messages yet" },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSize = FontSize.Normal,
            color = TransparentText20,
        )
        }
    )
}

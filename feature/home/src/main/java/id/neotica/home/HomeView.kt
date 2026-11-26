package id.neotica.home

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.painterResource
import id.neotica.domain.model.chat.Conversation
import id.neotica.domain.utils.convertLongToTime
import id.neotica.res.MR
import id.neotica.ui.shared.components.BasicScaffold
import id.neotica.ui.shared.components.ChatListItem
import id.neotica.ui.shared.components.ControlledNeoSearchBar
import id.neotica.ui.shared.components.LoadingIndicator
import id.neotica.ui.shared.components.NeoFAB
import id.neotica.ui.shared.theme.FontSize
import id.neotica.ui.shared.theme.TransparentText20
import org.jetbrains.compose.ui.tooling.preview.Preview

private const val DefaultAvatar =
    "https://www.pngarts.com/files/6/User-Avatar-in-Suit-PNG.png"

@Composable
fun HomeView(
    state: HomeState,
    paddingValues: PaddingValues,
    onQueryChange: (String) -> Unit,
    onRetry: () -> Unit,
    onConversationClick: (Conversation) -> Unit,
    onNewChat: () -> Unit,
) {
    BasicScaffold(
        topBarTitle = "Chats",
        paddingValues = paddingValues,
    ) {
        Box(Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxSize()) {
                Text(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp),
                    text = "Welcome, ${state.username.ifBlank { "there" }}!",
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
                ControlledNeoSearchBar(
                    value = state.query,
                    onValueChange = onQueryChange,
                    label = "Search chats",
                    padding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                )

                when {
                    state.isLoading -> LoadingIndicator()
                    state.error != null -> Column(Modifier.padding(16.dp)) {
                        Text(state.error, color = TransparentText20)
                        Text(
                            text = "Tap to retry",
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .clickable { onRetry() },
                        )
                    }
                    state.filteredConversations.isEmpty() -> Text(
                        modifier = Modifier.padding(16.dp),
                        text = "No conversations yet. Start a new chat.",
                        color = TransparentText20,
                    )
                    else -> LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 96.dp),
                    ) {
                        items(state.filteredConversations, key = { it.id }) { conversation ->
                            ConversationRow(conversation) { onConversationClick(conversation) }
                        }
                    }
                }
            }

            NeoFAB(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                onClick = onNewChat,
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

@Preview
@Composable
private fun HomeViewPreview() {
    HomeView(
        state = HomeState(
            username = "Ryo",
            conversations = listOf(
                Conversation(
                    id = "1",
                    peerId = "2",
                    peerUsername = "jdoe",
                    lastMessage = "see you tomorrow",
                    updatedAt = 1_780_000_000_000,
                ),
                Conversation(
                    id = "2",
                    peerId = "3",
                    peerUsername = "alex",
                    lastMessage = "checking in",
                    updatedAt = 1_780_000_010_000,
                ),
            ),
            isLoading = false,
        ),
        paddingValues = PaddingValues(),
        onQueryChange = {},
        onRetry = {},
        onConversationClick = {},
        onNewChat = {},
    )
}

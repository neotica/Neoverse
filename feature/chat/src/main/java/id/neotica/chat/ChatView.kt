package id.neotica.chat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import id.neotica.domain.model.chat.ChatMessage
import id.neotica.domain.utils.convertLongToTime
import id.neotica.ui.shared.components.BasicScaffold
import id.neotica.ui.shared.components.ToastHost
import id.neotica.ui.shared.components.topbar.DotsMenuDropDown
import id.neotica.ui.shared.components.topbar.DotsMenuItem
import id.neotica.ui.shared.theme.DarkBackground
import id.neotica.ui.shared.theme.DarkPrimary
import id.neotica.ui.shared.theme.DarkPrimaryTransparent2
import id.neotica.ui.shared.theme.FontSize
import id.neotica.ui.shared.theme.TransparentText20
import id.neotica.ui.shared.theme.TransparentText40
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ChatView(
    state: ChatState,
    paddingValues: PaddingValues,
    onDraftChange: (String) -> Unit,
    onSend: () -> Unit,
    onBack: () -> Unit,
    onDelete: () -> Unit,
    onToastDismissed: () -> Unit,
) {
    val listState = rememberLazyListState()

    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.scrollToItem(state.messages.lastIndex)
        }
    }

    BasicScaffold(
        topBarTitle = state.peerUsername,
        fullScreen = true,
        topBarVisibility = true,
        enableActionBar = true,
        customActionBar = onBack,
        paddingValues = paddingValues,
        trailingIcon = {
            DotsMenuDropDown(
                item = listOf(
                    DotsMenuItem("Delete chat", action = onDelete)
                ),
            )
        },
    ) {
        Column(Modifier.fillMaxSize()) {
            if (state.messages.isEmpty()) {
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No messages yet. Say hello!",
                        textAlign = TextAlign.Center,
                        color = TransparentText20,
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    state = listState,
                    contentPadding = PaddingValues(vertical = 8.dp),
                ) {
                    items(state.messages, key = { it.id }) { message ->
                        ChatBubble(
                            message = message,
                            isCurrentUser = message.senderId == state.currentUserId,
                        )
                    }
                }
            }

            MessageComposer(
                draft = state.draft,
                onDraftChange = onDraftChange,
                onSend = onSend,
                modifier = Modifier.padding(
                    start = 8.dp,
                    end = 8.dp,
                    bottom = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding(),
                ),
            )
        }
    }
    ToastHost(state.toastMessage, onToastDismissed)
}

@Composable
private fun MessageComposer(
    draft: String,
    onDraftChange: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors().copy(containerColor = DarkBackground),
        shape = RoundedCornerShape(24.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            BasicTextField(
                value = draft,
                onValueChange = onDraftChange,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                textStyle = TextStyle.Default.copy(fontSize = FontSize.Normal, color = White),
                cursorBrush = SolidColor(White),
            ) { inner ->
                Box {
                    if (draft.isEmpty()) {
                        Text("Message", color = TransparentText40, fontSize = FontSize.Normal)
                    }
                    inner()
                }
            }
            IconButton(
                onClick = {
                    if (draft.isNotBlank()) onSend()
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                )
            }
        }
    }
}

@Composable
private fun ChatBubble(
    message: ChatMessage,
    isCurrentUser: Boolean,
) {
    val bubbleColor = if (isCurrentUser) DarkPrimaryTransparent2 else DarkPrimary
    val alignment = if (isCurrentUser) Alignment.CenterEnd else Alignment.CenterStart

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = alignment,
    ) {
        Column(horizontalAlignment = if (isCurrentUser) Alignment.End else Alignment.Start) {
            Card(
                colors = CardDefaults.cardColors().copy(containerColor = bubbleColor),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(
                    text = message.body,
                    color = White,
                    modifier = Modifier.padding(8.dp),
                )
            }
            Text(
                text = convertLongToTime(message.createdAt, true),
                fontSize = FontSize.Smaller,
                color = TransparentText20,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}

@Preview
@Composable
private fun ChatViewPreview() {
    ChatView(
        state = ChatState(
            peerUsername = "jdoe",
            currentUserId = "me",
            messages = listOf(
                ChatMessage(id = "1", conversationId = "c", senderId = "jdoe", body = "see you tomorrow", createdAt = 1_780_000_000_000),
                ChatMessage(id = "2", conversationId = "c", senderId = "me", body = "sounds good", createdAt = 1_780_000_005_000),
            ),
            draft = "On my way",
        ),
        paddingValues = PaddingValues(),
        onDraftChange = {},
        onSend = {},
        onBack = {},
        onDelete = {},
        onToastDismissed = {},
    )
}

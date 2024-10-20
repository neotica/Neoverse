package id.neotica.neoversewear.presentation.screen.chat

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import id.neotica.domain.model.chat.Message
import id.neotica.domain.utils.convertLongToTime
import id.neotica.neoversewear.presentation.navigation.SpacerItem
import id.neotica.neoversewear.presentation.navigation.WearScreen
import id.neotica.ui.theme.DarkPrimary
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun WChatView(
    navController: NavController,
    args: WearScreen.ChatScreen,
    viewModel: WChatViewModel = koinViewModel()
) {
    val messages = viewModel.message.collectAsState()

    ChatMessages(
        messages = messages.value,
    ) { msg ->
        if (msg.isNotEmpty()) {
            viewModel.sendMessage(chatId = args.chatId, msg)
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ChatMessages(
    messages: List<Message>,
    onSendMessage: (String) -> Unit
) {
    val hideKeyboardController = LocalSoftwareKeyboardController.current

    val msg = remember { mutableStateOf("") }
    val scrollState = rememberScalingLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var enlargeTextField by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        coroutineScope.launch {
            scrollState.scrollToItem(messages.size + 1)
        }
        Column(Modifier.fillMaxSize()) {
            ScalingLazyColumn(
                modifier = Modifier.weight(1f),
                state = scrollState
            ) {
                items(messages) { messages ->
                    ChatBubble(message = messages)
                }
            }
            Surface(
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(32.dp)
            ) {
                Row(
                    modifier = Modifier
                        .background(DarkPrimary),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextField(
                        value = msg.value,
                        onValueChange = { msg.value = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text(text = "Type") },
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            hideKeyboardController?.hide()
                        })
                    )
                    IconButton(onClick = {
                        onSendMessage(msg.value)
                        msg.value = ""
                    }) {
                        enlargeTextField = false
                        Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "send")
                    }
                }
            }
            SpacerItem(8.dp)
        }
    }
}

@Composable
fun ChatBubble(message: Message) {
    val isCurrentUser = message.senderId == Firebase.auth.currentUser?.uid
    val bubbleColor = if (isCurrentUser) {
        Color.Blue
    } else {
        Color.Green
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)

    ) {
        val alignment = if (!isCurrentUser) Alignment.CenterStart else Alignment.CenterEnd

        Column(
            modifier = Modifier
                .align(alignment),
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .align(alignment)
                        .background(color = bubbleColor, shape = RoundedCornerShape(8.dp)),

                    ) {
                    androidx.wear.compose.material.Text(
                        text = message.message,
                        color = Color.White,
                        modifier = Modifier
                            .padding(8.dp)
                            .align(alignment),
                    )
                }
            }

            Box(Modifier.fillMaxWidth()) {
                androidx.wear.compose.material.Text(
                    modifier = Modifier.align(alignment),
                    fontSize = 12.sp,
                    text = convertLongToTime(message.createdAt, true)
                )
            }
        }
    }
}
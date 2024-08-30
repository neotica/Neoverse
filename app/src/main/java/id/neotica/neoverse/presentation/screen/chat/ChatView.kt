package id.neotica.neoverse.presentation.screen.chat

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import id.neotica.neoverse.data.model.Message
import id.neotica.neoverse.presentation.navigation.Screen
import id.neotica.neoverse.utils.convertLongToTime
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun ChatView(
    navController: NavController,
    paddingValues: PaddingValues,
    args: Screen.Chat,
    viewModel: ChatViewModel = koinViewModel()
) {
    val messages = viewModel.message.collectAsState()
    val parentMessages = viewModel.parentMessage.collectAsState()


    LaunchedEffect(key1 = true) {
        viewModel.getMessages(args.chatId)
    }

    Scaffold(modifier = Modifier
        .fillMaxSize()
        .padding(start = 8.dp, end = 8.dp, bottom = paddingValues.calculateBottomPadding())) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(it)) {
            ChatMessages(
                messages = messages.value,
                name2 = args.user2
            ) { msg ->

                viewModel.sendMessage(chatId = args.chatId, msg)
            }
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ChatMessages(
    modifier: Modifier = Modifier,
    name2: String,
    messages: List<Message>,
    viewModel: ChatViewModel = koinViewModel(),
    onSendMessage: (String) -> Unit
) {
    val hideKeyboardController = LocalSoftwareKeyboardController.current
    val parentMessages = viewModel.parentMessage.collectAsState()

    val msg = remember { mutableStateOf("") }
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        coroutineScope.launch {
            scrollState.scrollToItem(messages.size + 1)
        }
        Column(Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray), verticalAlignment = Alignment.CenterVertically
            ){
                Text(name2)
            }
            LazyColumn(
                modifier = Modifier
                    .height(200.dp)
                    .weight(1f),
                state = scrollState
            ) {
                items(messages) { messages ->
                    ChatBubble(message = messages)
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray), verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = msg.value,
                    onValueChange = { msg.value = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text(text = "Type a message") },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        hideKeyboardController?.hide()
                    })
                )
                IconButton(onClick = {
                    onSendMessage(msg.value)
                    msg.value = ""
                }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "send")
                }
            }
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
                    modifier = Modifier.align(alignment)
                        .background(color = bubbleColor, shape = RoundedCornerShape(8.dp)),

                    ) {
                    Text(
                        text = message.message,
                        color = Color.White,
                        modifier = Modifier
                            .padding(8.dp)
                            .align(alignment),
                    )
                }
            }

            Box(Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier.align(alignment),
                    fontSize = 12.sp,
                    text = convertLongToTime(message.createdAt, true)
                )
            }
        }
    }
}
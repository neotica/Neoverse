package id.neotica.neoverse.presentation.screen.chat

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
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
import id.neotica.domain.model.chat.Message
import id.neotica.domain.utils.convertLongToTime
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.navigation.RootScreen
import id.neotica.ui.navigation.Screen
import id.neotica.ui.theme.DarkPrimary
import id.neotica.ui.theme.Transparent
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

    BackHandler {
        navController.navigate(RootScreen.HomeNav) {
            popUpTo(RootScreen.HomeNav) {
                inclusive = true
            }
            launchSingleTop
        }
    }

    BasicScaffold(
        topBarTitle = args.user2,
        customActionBar = {
            navController.navigate(RootScreen.HomeNav) {
                popUpTo(RootScreen.HomeNav) {
                    inclusive = true
                }
                launchSingleTop
            }
        },
        navController = navController,
        fullScreen = true
    ) {
        ChatMessages(
            messages = messages.value,
        ) { msg ->
            if (msg.isNotEmpty()) {
                viewModel.sendMessage(chatId = args.chatId, msg)
            }
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
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        coroutineScope.launch {
            scrollState.scrollToItem(messages.size + 1)
        }
        Column(Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f),
                state = scrollState,
                contentPadding = PaddingValues(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() * 2)
            ) {
                items(messages) { messages ->
                    ChatBubble(message = messages)
                }
            }
            Surface(
                modifier = Modifier
                    .padding(start = 4.dp, end = 4.dp, top = 4.dp, bottom = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding())
                ,
                color = Transparent,
                shape = RoundedCornerShape(32.dp)
            ) {
                Card(
                    Modifier,
                    shape = RoundedCornerShape(32.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .background(DarkPrimary)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
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
    }
}

@Composable
fun ChatBubble(message: Message) {
    val isCurrentUser = message.senderId == Firebase.auth.currentUser?.uid
    val bubbleColor = if (isCurrentUser) {
        Color.Blue
    } else {
        Color.Red
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
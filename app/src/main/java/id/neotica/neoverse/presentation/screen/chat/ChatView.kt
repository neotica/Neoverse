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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import id.neotica.domain.model.chat.Message
import id.neotica.domain.utils.convertLongToTime
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.scroll.scrollBottomValue
import id.neotica.ui.navigation.RootScreen
import id.neotica.ui.navigation.Screen
import id.neotica.ui.theme.BlackTransparent40
import id.neotica.ui.theme.DarkPrimary
import id.neotica.ui.theme.DarkPrimaryTransparent2
import id.neotica.ui.theme.FontSize
import id.neotica.ui.theme.Transparent
import id.neotica.ui.theme.TransparentText
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun ChatView(
    navController: NavController,
    args: Screen.Chat,
    viewModel: ChatViewModel = koinViewModel()
) {
    val messages = viewModel.message.collectAsState()
    val listState = rememberLazyListState()

    BackHandler {
        navController.navigate(RootScreen.HomeNav) {
            popUpTo(RootScreen.HomeNav) {
                inclusive = true
            }
            launchSingleTop
        }
    }

    val layoutInfo by remember { derivedStateOf { listState.layoutInfo } }

    val scrollTrue = when {
        layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1 -> true
        !scrollBottomValue(listState) -> true
        else -> false
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
        topBarVisibility = scrollTrue,
        fullScreen = true
    ) {
        ChatMessages(
            messages = messages.value,
            listState = listState
        ) { msg ->
            if (msg.isNotEmpty() && args.user2.isNotEmpty()) {
                viewModel.sendMessage(chatId = args.chatId, msg)
            }
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ChatMessages(
    messages: List<Message>,
    listState: LazyListState,
    onSendMessage: (String) -> Unit
) {
    val msg = remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        coroutineScope.launch {
            listState.scrollToItem(messages.size + 1)
        }
        Column(Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f),
                state = listState,
                contentPadding = PaddingValues(
                    bottom = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding() + 46.dp,
                    top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() * 2
                )
            ) {
                items(messages) { messages ->
                    ChatBubble(message = messages)
                }
            }
        }
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(
                    start = 4.dp,
                    end = 4.dp,
                    top = 4.dp,
                    bottom = WindowInsets.systemBars
                        .asPaddingValues()
                        .calculateBottomPadding()
                )
            ,
            color = Transparent,
            shape = RoundedCornerShape(32.dp)
        ) {
            Card(
                Modifier.height(46.dp),
                shape = RoundedCornerShape(32.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .background(DarkPrimary)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    BasicTextField(
                        value = msg.value,
                        onValueChange = { msg.value = it },
                        modifier = Modifier
                            .background(color = DarkPrimaryTransparent2)
                            .fillMaxWidth()
                            .weight(1f),
                        textStyle = TextStyle.Default.copy(
                            fontSize = FontSize.Normal,
                            color = Color.White,
                        ),
                        cursorBrush = SolidColor(DarkPrimary),
                    ) { innerTextField ->
                        Box(
                            Modifier
                                .padding(start = 12.dp, end = 12.dp, top = 5.dp * 2, bottom = 5.dp)
                                .height(46.dp)
                        ) {
                            if (msg.value.isEmpty()) {
                                Text(
                                    text = "Message",
                                    color = TransparentText,
                                    fontSize = FontSize.Normal
                                )
                            }
                            innerTextField()
                        }
                    }
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

@Composable
fun ChatBubble(message: Message) {
    val isCurrentUser = message.senderId == Firebase.auth.currentUser?.uid
    val bubbleColor = if (isCurrentUser) {
        DarkPrimaryTransparent2
    } else {
        BlackTransparent40
    }
    val textColor = if (isCurrentUser) {
        Color.White
    } else {
        Color.White
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
                        color = textColor,
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
package id.neotica.neoverse.presentation.screen.chat

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import id.neotica.domain.model.chat.Message
import id.neotica.domain.utils.convertLongToTime
import id.neotica.ui.R
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.DropdownItem
import id.neotica.ui.components.scroll.scrollBottomValue
import id.neotica.ui.navigation.RootScreen
import id.neotica.ui.navigation.Screen
import id.neotica.ui.theme.BlackTransparent40
import id.neotica.ui.theme.DarkPrimary
import id.neotica.ui.theme.DarkPrimaryTransparent2
import id.neotica.ui.theme.DarkPrimaryTransparent80
import id.neotica.ui.theme.FontSize
import id.neotica.ui.theme.NeoPadding
import id.neotica.ui.theme.Transparent
import id.neotica.ui.theme.TransparentText40
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatView(
    navController: NavController,
    args: Screen.Chat,
    viewModel: ChatViewModel = koinViewModel()
) {
    val messages = viewModel.message.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(messages.value) {
        if (messages.value.isNotEmpty()) {
            listState.scrollToItem(messages.value.size - 1)
        }
    }

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
        messages.value.isEmpty() -> true
        else -> false
    }
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

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
        fullScreen = true,
        trailingIcon = {
            var expanded by remember { mutableStateOf(false) }

            Box {
                Icon(
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .combinedClickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(bounded = true, color = White),
                            onClick = {
                                expanded = !expanded
                            },
                            onLongClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                Toast
                                    .makeText(context, "More", Toast.LENGTH_SHORT)
                                    .show()
                            },
                        ),
                    imageVector = ImageVector.vectorResource(R.drawable.three_dots),
                    contentDescription = "",
                    tint = White
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    containerColor = DarkPrimaryTransparent2,
                ) {
                    DropdownItem("Delete Chat", White) {
                        viewModel.deleteChatroom()
                        navController.navigate(RootScreen.HomeNav) {
                            popUpTo(RootScreen.HomeNav) {
                                inclusive = true
                            }
                            launchSingleTop
                        }
                    }
                }
            }
        }
    ) {
        ChatMessages(
            messages = messages.value,
            listState = listState,
        ) { msg ->
            if (msg.isNotEmpty() && args.user2.isNotEmpty()) {
                viewModel.sendMessage(chatId = args.chatId, msg)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ChatMessages(
    messages: List<Message>,
    listState: LazyListState,
    onSendMessage: (String) -> Unit
) {
    val msg = remember { mutableStateOf("") }
    var showCard by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(500L)
        showCard = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            if (messages.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f),
                    state = listState,
                    contentPadding = PaddingValues(
                        bottom = NeoPadding.systemBarPadding + 46.dp,
                        top = NeoPadding.statusBarPadding + TopAppBarDefaults.TopAppBarExpandedHeight
                    )
                ) {
                    items(messages) { messages ->
                        ChatBubble(message = messages)
                    }
                }
            } else {
                androidx.compose.animation.AnimatedVisibility(
                    visible = messages.isEmpty() && showCard,
                    enter = fadeIn(tween(500))
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            modifier = Modifier
                                .padding(16.dp)
                                .align(Alignment.Center),
                            colors = CardDefaults.cardColors().copy(containerColor = DarkPrimaryTransparent80)
                        ) {
                            Text(
                                modifier = Modifier.padding(8.dp),
                                text = "Whoops! It's a bit empty around here. Try sending a message! 😣",
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(
                    start = 8.dp,
                    end = 8.dp,
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
                            color = White,
                        ),
                        cursorBrush = SolidColor(White),
                    ) { innerTextField ->
                        Box(
                            Modifier
                                .padding(start = 12.dp, end = 12.dp, top = 5.dp * 2, bottom = 5.dp)
                                .height(46.dp)
                        ) {
                            if (msg.value.isEmpty()) {
                                Text(
                                    text = "Message",
                                    color = TransparentText40,
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
        White
    } else {
        White
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
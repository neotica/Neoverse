package id.neotica.chat

import android.annotation.SuppressLint
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dev.chrisbanes.haze.HazeProgressive
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import id.neotica.domain.model.chat.Message
import id.neotica.domain.utils.convertLongToTime
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.scroll.scrollBottomValue
import id.neotica.ui.components.topbar.DotsMenuDropDown
import id.neotica.ui.components.topbar.DotsMenuItem
import id.neotica.ui.navigation.RootScreen
import id.neotica.ui.navigation.Screen
import id.neotica.ui.theme.BlackTransparent40
import id.neotica.ui.theme.DarkPrimary
import id.neotica.ui.theme.DarkPrimaryTransparent2
import id.neotica.ui.theme.DarkPrimaryTransparent40
import id.neotica.ui.theme.DarkPrimaryTransparent80
import id.neotica.ui.theme.FontSize
import id.neotica.ui.theme.NeoPadding
import id.neotica.ui.theme.Transparent
import id.neotica.ui.theme.TransparentText20
import id.neotica.ui.theme.TransparentText40
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import androidx.core.net.toUri

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatView(
    navController: NavController,
    args: Screen.Chat,
    defaultText: String = "",
    viewModel: ChatViewModel = koinViewModel()
) {
    val messages = viewModel.message.collectAsState()
    val listState = rememberLazyListState()
    val activity = LocalActivity.current

    fun finishApp() {
        if (defaultText.isEmpty()) {
            navController.navigate(RootScreen.HomeNav) {
                popUpTo(RootScreen.HomeNav) {
                    inclusive = true
                }
                launchSingleTop

            }
        } else {
            activity?.finish()
        }
    }

    LaunchedEffect(messages.value) {
        if (messages.value.isNotEmpty()) {
            listState.scrollToItem(messages.value.size - 1)
        }
    }

    LaunchedEffect(Unit) {
        if (defaultText.isNotEmpty()) viewModel.sendMessage(chatId = args.chatId, defaultText)
    }

    BackHandler {
        finishApp()
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
    val hazeState = remember { HazeState() }

    BasicScaffold(
        topBarTitle = args.user2,
        enableActionBar = true,
        customActionBar = {
            finishApp()
        },
        navController = navController,
        topBarVisibility = true,
//        topBarBackground = scrollTrue,
        topBarModifier = Modifier
            .hazeEffect(hazeState) {
                blurEnabled = true
                blurRadius = 10.dp
                noiseFactor = 0.15f
                backgroundColor = DarkPrimaryTransparent80
            }
            .padding(top = NeoPadding.topBarImePadding)
            .fillMaxWidth(),
        fullScreen = true,
        trailingIcon = {
            DotsMenuDropDown(
                item = listOf(
                    DotsMenuItem(
                        "Delete Chat",
                        {
                            viewModel.deleteChatroom()
                            navController.navigate(RootScreen.HomeNav) {
                                popUpTo(RootScreen.HomeNav) {
                                    inclusive = true
                                }
                                launchSingleTop
                            }
                        }
                    )
                ),
                onLongClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    Toast
                        .makeText(context, "More", Toast.LENGTH_SHORT)
                        .show()
                }
            )
        }
    ) {
        Box {
            ChatMessages(
                messages = messages.value,
                listState = listState,
                hazeState
            ) { msg ->
                if (msg.isNotEmpty() && args.user2.isNotEmpty()) {
                    viewModel.sendMessage(chatId = args.chatId, msg)
                }
            }
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ChatMessages(
    messages: List<Message>,
    listState: LazyListState,
    hazeState: HazeState,
    onSendMessage: (String) -> Unit
) {
    val msg = remember { mutableStateOf("") }
    var showCard by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        delay(500L)
        showCard = true
    }

    Box(
        Modifier.pointerInput(Unit) {
            detectTapGestures { focusManager.clearFocus() }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .hazeSource(state = hazeState)
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
                            bottom = NeoPadding.systemBarPadding + 46.dp + 8.dp,
                            top = NeoPadding.topBarPadding + 8.dp
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
        }
        Surface(
            modifier = Modifier
                .hazeEffect(hazeState) {
                    blurEnabled = true
                    blurRadius = 10.dp
                    noiseFactor = 0.15f
                    backgroundColor = Transparent
                    progressive =
                        HazeProgressive.verticalGradient(startIntensity = 0f, endIntensity = 1f)
                }
                .align(Alignment.BottomCenter)
                .padding(
                    start = 8.dp,
                    end = 8.dp,
                    bottom = WindowInsets.systemBars
                        .asPaddingValues()
                        .calculateBottomPadding()
                )
            ,
            color = Transparent,
            shape = RoundedCornerShape(32.dp)
        ) {
            Card(
                Modifier
                    .height(46.dp),
                colors = CardDefaults.cardColors().copy(containerColor = Transparent),
                shape = RoundedCornerShape(32.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .background(DarkPrimaryTransparent40)
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
                    Box(Modifier.background(color = DarkPrimary)) {
                        IconButton(onClick = {
                            onSendMessage(msg.value)
                            msg.value = ""
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "send"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: Message, viewModel: ChatViewModel = koinViewModel()) {
    val isCurrentUser = message.senderId == viewModel.getUserId()
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
            Card(
                colors = CardDefaults.cardColors().copy(containerColor = bubbleColor),
                shape =  RoundedCornerShape(8.dp),
            ) {
                Box {
                    Column {
                        Row {
                            val context = LocalContext.current

                            // Function to detect URLs and apply clickable styling
                            val annotatedText = buildAnnotatedString {
                                val regex = "(https?://\\S+)".toRegex()
                                var lastIndex = 0

                                regex.findAll(message.message).forEach { matchResult ->
                                    val url = matchResult.value
                                    val start = matchResult.range.first
                                    val end = matchResult.range.last + 1

                                    // Add regular text before the URL
                                    append(message.message.substring(lastIndex, start))

                                    // Apply color to URL
                                    pushStringAnnotation(tag = "URL", annotation = url)
                                    withStyle(style = SpanStyle(color = TransparentText40, textDecoration = TextDecoration.Underline)) {
                                        append(url)
                                    }
                                    pop()

                                    lastIndex = end
                                }

                                if (lastIndex < message.message.length) {
                                    append(message.message.substring(lastIndex))
                                }
                            }

                            SelectionContainer {
                                Text(
                                    text = annotatedText,
                                    color = textColor,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .weight(1f, fill = false)
                                        .clickable {
                                            val filteredUrls =
                                                "(https?://\\S+)".toRegex().findAll(message.message)
                                                    .map { it.value }
                                                    .joinToString(" ")
                                            val intent =
                                                Intent(Intent.ACTION_VIEW, filteredUrls.toUri())
                                            if (filteredUrls.isNotEmpty()) context.startActivity(
                                                intent
                                            )
                                        }
                                )
                            }

                            Text(
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .align(Alignment.Bottom),
                                fontSize = 12.sp,
                                maxLines = 1,
                                color = TransparentText20,
                                text = convertLongToTime(message.createdAt, true)
                            )
                        }
                    }
                }
            }
        }
    }
}
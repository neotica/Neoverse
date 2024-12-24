package id.neotica.raven

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import id.neotica.domain.ApiResult
import id.neotica.domain.model.chat.Message
import id.neotica.domain.model.raven.Raven
import id.neotica.domain.utils.convertLongToTime
import id.neotica.raven.utils.ravenIntro
import id.neotica.raven.utils.unknownCommand
import id.neotica.ui.R
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.DropdownItem
import id.neotica.ui.components.scroll.scrollBottomValue
import id.neotica.ui.navigation.RootScreen
import id.neotica.ui.navigation.WorldScreen
import id.neotica.ui.theme.BlackTransparent40
import id.neotica.ui.theme.DarkPrimary
import id.neotica.ui.theme.DarkPrimaryTransparent2
import id.neotica.ui.theme.DarkPrimaryTransparent80
import id.neotica.ui.theme.FontSize
import id.neotica.ui.theme.NeoPadding
import id.neotica.ui.theme.Transparent
import id.neotica.ui.theme.TransparentText20
import id.neotica.ui.theme.TransparentText40
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RavenChatView(
    navController: NavController,
    viewModel: RavenViewModel = koinViewModel()
) {
    val messages = remember { mutableStateListOf(Message(message = ravenIntro)) }
    val listState = rememberLazyListState()

    val words = remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        viewModel.words.collect { result ->
            Log.d("ravenview", result.toString())
            when(result) {
                is ApiResult.Loading -> {}
                is ApiResult.Success -> {
                    if (result.data != null) {
                        if (result.data!!.isEmpty()) messages.add(Message(message = "Sorry, what?"))
                        words.value = result.data!!.random()
                        messages.add(Message(message = words.value))
                    }
                    Log.d("ravenview", words.value)
                }
                is ApiResult.Error -> {
                    messages.add(Message(message = unknownCommand))
                    val lastSecondIndex = messages.size - 2
                    val lastThirdIndex = messages.size - 3
                    if (messages[lastThirdIndex].message != unknownCommand && messages[lastThirdIndex].message != ravenIntro && messages.isNotEmpty()) {

                        Log.d("2index", messages[lastThirdIndex].message)
                        Log.d("3index", messages[lastSecondIndex].message)

                        viewModel.postWord(
                            Raven(
                                word = messages[lastThirdIndex].message,
                                value = messages[lastSecondIndex].message
                            )
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.scrollToItem(messages.size - 1)
        }
    }


    val layoutInfo by remember { derivedStateOf { listState.layoutInfo } }

    val scrollTrue = when {
        layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1 -> true
        !scrollBottomValue(listState) -> true
        messages.isEmpty() -> true
        else -> false
    }
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    BasicScaffold(
        topBarTitle = "Raven Chat",
        enableActionBar = true,
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
                    DropdownItem("Train Raven", White) {
                        navController.navigate(WorldScreen.RavenScreen)
                    }
                    DropdownItem("Delete Chat", White) {
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
            messages = messages,
            listState = listState,
        ) { msg ->
            messages.add(Message(message = msg, senderId = viewModel.currentUser))
            if (msg.isNotEmpty()) {
                viewModel.getWords(msg)
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
                        bottom = NeoPadding.systemBarPadding + 46.dp + 8.dp,
                        top = NeoPadding.topBarPadding
                    )
                ) {
                    items(messages) { messages ->
                        val ravenMessage = messages.copy()
                        ChatBubble(message = ravenMessage)
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
fun ChatBubble(message: Message, viewModel: RavenViewModel = koinViewModel()) {
    val isCurrentUser = message.senderId == viewModel.currentUser
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
                            Text(
                                text = message.message,
                                color = textColor,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .weight(1f, fill = false)
                            )
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
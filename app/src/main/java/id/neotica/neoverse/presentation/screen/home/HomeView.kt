package id.neotica.neoverse.presentation.screen.home

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import id.neotica.domain.ApiResult
import id.neotica.domain.model.User
import id.neotica.domain.model.chat.ChatRoom
import id.neotica.domain.model.chat.ParentNeo
import id.neotica.domain.utils.convertLongToHour
import id.neotica.domain.utils.filteredRoom
import id.neotica.domain.utils.returnFriendName
import id.neotica.ui.components.ChatListItem
import id.neotica.ui.components.ImageCoil
import id.neotica.ui.navigation.RootScreen
import id.neotica.ui.navigation.Screen
import id.neotica.ui.theme.DarkPrimaryTransparent2
import id.neotica.ui.theme.FontSize
import id.neotica.ui.theme.TransparentText
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: HomeViewModel = koinViewModel(),
) {
    var isPressed by remember { mutableStateOf(false) }
    val parentChatrooms = remember { mutableStateOf<List<ParentNeo<ChatRoom>>>(emptyList()) }

    var friendList by remember { mutableStateOf<List<User>>(emptyList()) }


    var isLoading by remember { mutableStateOf(false) }
    var isLoadingChats by remember { mutableStateOf(false) }
    var currentuser by remember { mutableStateOf<User?>(null) }

    val context = LocalContext.current

    LaunchedEffect(isPressed) {
        isPressed = false
        if (viewModel.isLoggedIn()) {
            if (!viewModel.isUserVerified()) navController.navigate(Screen.VerifyEmailScreen) {
                popUpTo(RootScreen.HomeNav) {
                    inclusive = true
                }
                launchSingleTop
            }
        }
        viewModel.user.collect { result ->
            when(result) {
                is ApiResult.Loading -> isLoading = true
                is ApiResult.Success -> {
                    isLoading = false
                    currentuser = result.data
                }
                is ApiResult.Error -> {
                    isLoading = false
                }
            }
        }
    }

    LaunchedEffect(isPressed) {
        viewModel.chatroom.collect { result ->
            when(result) {
                is ApiResult.Loading -> {
                    isLoadingChats = true
                }
                is ApiResult.Success -> {
                    isLoadingChats = false
                    isPressed = false

                    viewModel.chatList.forEach { _ ->
                        parentChatrooms.value = result.data!!
                        viewModel.getFriendsRoom().collect {
                            friendList = it
                        }
                    }
                }
                is ApiResult.Error -> {
                    isPressed = false
                    isLoadingChats = false
                }
            }
        }
    }

    val nestedState = rememberNestedScrollInteropConnection()
    val listState = rememberLazyListState()
    val showTopCard = remember { derivedStateOf { listState.firstVisibleItemScrollOffset == 0 } }
    val haptic = LocalHapticFeedback.current

    Scaffold (
        modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing),
    ) { it ->
        Box(
            Modifier.fillMaxSize()
                .padding(PaddingValues(bottom = paddingValues.calculateBottomPadding()))
            ,
        ) {
            FloatingActionButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
                ,
                containerColor = DarkPrimaryTransparent2,
                onClick = {
                    navController.navigate(Screen.AddChatScreen)
                }
            ) {
                Text("+")
            }
        }
        Column(Modifier.padding(it)) {
            AnimatedVisibility(
                visible = showTopCard.value,
                enter = expandVertically(),
                exit = shrinkVertically(),
            ) {
                Column {
                    Card(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .fillMaxWidth(),
                        colors = CardDefaults.cardColors().copy(
                            containerColor = DarkPrimaryTransparent2
                        ),
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(8.dp)
                        ) {
                            Column(
                                Modifier.weight(4f)
                            ) {
                                Text(
                                    text = "Welcome, ${currentuser?.username ?: ""}!",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontSize = FontSize.Big,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "You have ${viewModel.currentAura()} Auras.",
                                    fontSize = FontSize.Small,
                                    color = TransparentText
                                )
                            }
                            Column(
                                Modifier
                                    .weight(1f)
                                    .align(Alignment.CenterVertically)
                            ) {
                                currentuser?.let {
                                    ImageCoil(
                                        url = it.imageUrl,
                                        round = true,
                                        size = 55.dp,
                                        contentAlignment = Alignment.CenterEnd,
                                        onLongClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            Toast
                                                .makeText(context, "Go to Profile", Toast.LENGTH_SHORT)
                                                .show()
                                        }
                                    ) {
                                        navController.navigate(RootScreen.AuthNav)
                                    }
                                }
                            }
                        }
                    }
                    Spacer(Modifier.padding(4.dp))
                }
            }
            PullToRefreshBox(
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(nestedState),
                isRefreshing = isLoadingChats,
                onRefresh = {
                    isPressed = true
                    viewModel.getChatroom()
                }
            ) {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(bottom = paddingValues.calculateBottomPadding())
                ) {
//                    item { for(i in 1..30) Text("hshjkcvhbxdbjkcvbcvxcvxdvx") }
                    if(!isLoadingChats) {
                        items(parentChatrooms.value.filter { channels ->
                            val current = currentuser?.username.toString()
                            filteredRoom(current, channels.value.user1, channels.value.user2)
                        }) { channelItemParent ->
                            val chatName = returnFriendName(
                                currentuser?.username.toString(),
                                channelItemParent.value.user1,
                                channelItemParent.value.user2
                            )
                            val friendInstance = friendList.firstOrNull {
                                it.username == chatName
                            }

                            ChatListItem(
                                name = chatName,
                                image = friendInstance?.imageUrl.toString(),
                                enableLongClick = true,
                                onLongClick = {
                                    isPressed = true
                                    viewModel.deleteChatroom(channelItemParent.value)
                                },
                                alertTitle = "Clear Chat with $chatName?",
                                alertButton = "Clear",
                                content = {
                                    channelItemParent.value.let {
                                        Text(
                                            text = chatName,
                                            textAlign = TextAlign.Center,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (it.lastMessage.isNotEmpty()) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = channelItemParent.value.lastMessage,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    modifier = Modifier.widthIn(max = 274.dp)
                                                )
                                                Text(
                                                    text = convertLongToHour(it.lastOpened),
                                                    maxLines = 1,
                                                    fontSize = 12.sp
                                                )
                                            }
                                        } else {
                                            Text(
                                                text = "No message has been sent.",
                                                fontStyle = FontStyle.Italic
                                            )
                                        }
                                    }
                                }
                            ) {
                                currentuser?.let {
                                    navController.navigate(
                                        Screen.Chat(
                                            chatroomId = channelItemParent.value.id,
                                            chatId = channelItemParent.value.name,
                                            user2 = chatName
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


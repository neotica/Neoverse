package id.neotica.neoverse.presentation.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
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
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.ChatListItem
import id.neotica.ui.navigation.RootScreen
import id.neotica.ui.navigation.Screen
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
    val nestedState = rememberNestedScrollInteropConnection()

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

    BasicScaffold(topBarTitle = "Home", enableEye = true) {
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
            LazyColumn {
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


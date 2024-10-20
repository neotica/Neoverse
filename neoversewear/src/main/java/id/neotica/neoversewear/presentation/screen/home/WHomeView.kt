package id.neotica.neoversewear.presentation.screen.home

import android.util.Log
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.material.Text
import id.neotica.domain.ApiResult
import id.neotica.domain.model.User
import id.neotica.domain.model.chat.ChatRoom
import id.neotica.domain.model.chat.ParentNeo
import id.neotica.domain.utils.filteredRoom
import id.neotica.domain.utils.returnFriendName
import id.neotica.neoversewear.presentation.components.WChatListItem
import id.neotica.neoversewear.presentation.navigation.WearScreen
import id.neotica.ui.navigation.RootScreen
import id.neotica.ui.navigation.Screen
import org.koin.androidx.compose.koinViewModel

@Composable
fun WearHomeScreen(navController: NavController, viewModel: WHomeViewModel = koinViewModel()) {

    var isPressed by remember { mutableStateOf(false) }
    val parentChatrooms = remember { mutableStateOf<List<ParentNeo<ChatRoom>>>(emptyList()) }

    var friendList by remember { mutableStateOf<List<User>>(emptyList()) }
    var friendList2 by remember { mutableStateOf<List<User>>(emptyList()) }
    var isFriendEmpty by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var isLoadingChats by remember { mutableStateOf(false) }
    var currentuser by remember { mutableStateOf<User?>(null) }

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
        if (!isPressed) {
            viewModel.getFriendsRoom().collect {
                if (it.isNotEmpty()) {
                    friendList = it
                    Log.d("friend", "didnt collect")
                    isPressed = false

                } else {
                    isFriendEmpty = true
                    isPressed = false
                    viewModel.getFriends()
                    Log.d("friend", "else")

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

    LaunchedEffect(isPressed) {

        viewModel.friendList.collect { result ->
            Log.d("friendList", result.toString())
            when(result) {
                is ApiResult.Loading -> isLoading = true
                is ApiResult.Success -> {
                    isPressed = false
                    isLoading = false
                    result.data?.let {
                        friendList2 = result.data!!
                        viewModel.addFriendList(friendList2)
                    }
                }
                is ApiResult.Error -> {
                    isPressed = false
                    isLoading = false
                    Log.d("neotica", result.errorMessage.toString())
                }
            }
        }
    }

    ScalingLazyColumn {
        item {
            LazyRow {
                items(parentChatrooms.value.filter { channels ->
                    val current = currentuser?.username.toString()
                    filteredRoom(current, channels.value.user1, channels.value.user2)
                }){
                        channelItemParent ->
                    val chatName = returnFriendName(
                        currentuser?.username.toString(),
                        channelItemParent.value.user1,
                        channelItemParent.value.user2
                    )
                    val friendInstance = friendList.firstOrNull {
                        it.username == chatName
                    }
                    WChatListItem(
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
                            Text(
                                text = chatName,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    ) {
                        currentuser?.let {
                            val wear = WearScreen.ChatScreen(
                                chatroomId = channelItemParent.value.id,
                                chatId = channelItemParent.value.name,
                                user2 = chatName
                            )
                            navController.navigate(
                                "WearScreen.ChatScreen/${wear.chatId}/${wear.user2}/${wear.chatroomId}"
                            )
                        }
                    }

                }
            }
        }
    }
}
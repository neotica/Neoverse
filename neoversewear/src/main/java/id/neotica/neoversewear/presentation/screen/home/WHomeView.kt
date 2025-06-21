package id.neotica.neoversewear.presentation.screen.home

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.Text
import id.neotica.domain.ApiResult
import id.neotica.domain.model.User
import id.neotica.domain.model.chat.ChatRoom
import id.neotica.domain.model.chat.ParentNeo
import id.neotica.domain.utils.convertLongToTime
import id.neotica.domain.utils.filteredRoom
import id.neotica.domain.utils.returnFriendName
import id.neotica.neoversewear.presentation.components.BasicWearScreen
import id.neotica.neoversewear.presentation.components.SpacerItem
import id.neotica.neoversewear.presentation.components.WChatListItem
import id.neotica.neoversewear.presentation.components.WLoadingIndicator
import id.neotica.neoversewear.presentation.navigation.WearScreen
import id.neotica.routes.RootScreen
import id.neotica.routes.Screen
import id.neotica.ui.components.image.ImageCoil
import org.koin.compose.viewmodel.koinViewModel

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
                    isPressed = false

                } else {
                    isFriendEmpty = true
                    isPressed = false
                    viewModel.getFriends()

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
                            if (it.isNotEmpty()) {
                                friendList = it
                                isPressed = false
                            } else {
                                isPressed = false
                                viewModel.getFriends()
                                Log.d("friend", "else")
                            }
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
                }
            }
        }
    }

    BasicWearScreen {
        item { SpacerItem() }
        if (isLoadingChats) item { WLoadingIndicator() }
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
            Log.d("friendinstance", friendList.toString())
            Log.d("friendinstance2", friendInstance.toString())
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ImageCoil(
                            url = friendInstance?.imageUrl.toString(),
                            fillMaxWidth = false,
                            size = 32.dp,
                            contentAlignment = Alignment.CenterStart
                        )
                        SpacerItem(4.dp)
                        Column {
                            Text(
                                text = chatName,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold
                            )
                            channelItemParent.value.let {
                                if (it.lastMessage.isNotEmpty()) {
                                    Text(
                                        text = channelItemParent.value.lastMessage,
                                        fontWeight = FontWeight.Light,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.widthIn(max = 200.dp)
                                    )
                                    Text(
                                        text = convertLongToTime(it.lastOpened),
                                        fontWeight = FontWeight.Light,
                                        maxLines = 1,
                                        fontSize = 8.sp,
                                        textAlign = TextAlign.End,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                } else {
                                    Text(
                                        text = "No message has been sent.",
                                        fontStyle = FontStyle.Italic
                                    )
                                }
                            }

                        }
                    }
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
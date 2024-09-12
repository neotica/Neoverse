package id.neotica.neoverse.presentation.screen.dump

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import id.neotica.neoverse.data.local.entity.CurrentUserEntity
import id.neotica.neoverse.data.local.entity.FriendEntity
import id.neotica.neoverse.data.remote.model.ChatRoom
import id.neotica.neoverse.data.remote.model.ParentNeo
import id.neotica.neoverse.data.remote.model.User
import id.neotica.neoverse.data.remote.ApiResult
import id.neotica.neoverse.presentation.navigation.Screen
import id.neotica.neoverse.presentation.screen.home.DumpViewModel
import id.neotica.neoverse.presentation.screen.home.HomeViewModel
import id.neotica.neoverse.presentation.ui.components.BasicScaffold
import id.neotica.neoverse.presentation.ui.components.ImageCoil
import id.neotica.neoverse.presentation.ui.components.ProfileCard
import org.koin.androidx.compose.koinViewModel

@Composable
fun DumpView(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: DumpViewModel = koinViewModel(),
) {
    val isPressed = remember { mutableStateOf(false) }
    Log.d("ispressed", isPressed.value.toString())
    var userList by remember { mutableStateOf<List<User>>(emptyList()) }
    val chatrooms = remember { mutableStateOf<List<ChatRoom>>(emptyList()) }
    val parentChatrooms = remember { mutableStateOf<List<ParentNeo<ChatRoom>>>(emptyList()) }
    var friendList by remember { mutableStateOf<List<FriendEntity>>(emptyList()) }


    var isLoading by remember { mutableStateOf(false) }
    var isLoadingChats by remember { mutableStateOf(false) }
    var currentuser by remember { mutableStateOf<CurrentUserEntity?>(null) }

    LaunchedEffect(isPressed.value) {
        viewModel.userList.collect { result ->
            when(result) {
                is ApiResult.Loading -> isLoading = true
                is ApiResult.Success -> {
                    isLoading = false
                    result.data?.let { userList = result.data }
                    Log.d("neotica", result.data?.get(0)?.username.toString())
                }
                is ApiResult.Error -> {
                    isLoading = false
                    Log.d("neotica", result.errorMessage.toString())
                }
            }
        }
    }

    LaunchedEffect(isPressed.value) {
        if (viewModel.isLoggedIn()) {
            if (!viewModel.isUserVerified()) navController.navigate(Screen.VerifyEmailScreen)
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

    LaunchedEffect(isPressed.value) {

        viewModel.chatroom.collect { result ->
            Log.d("chatroomcollect", result.data.toString())
            when(result) {
                is ApiResult.Loading -> {
//                    Log.d("chatroomcollect", "loading")

                    isPressed.value = true
                    isLoadingChats = true
                }
                is ApiResult.Success -> {
//                    Log.d("chatroomcollect", "scs")
                    isPressed.value = false
                    isLoadingChats = false
                    val list = mutableListOf<ChatRoom>()
                    result.data?.forEach { chatResult ->
                        list.add(chatResult.value)
                        parentChatrooms.value = result.data
                        chatrooms.value = list
                        viewModel.getFriendsRoom().collect {
                            friendList = it
                            val username = currentuser?.username.toString()
                            Log.d("home username", currentuser?.username.toString())
                            /*val friendFiltered = it.map { friend ->
                                if (username != chatResult.value.user2) {
                                    val same = friend.username == chatResult.value.user1

                                }
                                else {
                                    val same = friend.username == chatResult.value.user2
                                }
                            }*/
                        }
                    }
                }
                is ApiResult.Error -> {
//                    Log.d("chatroomcollect", "error")

                    isLoadingChats = false
                }
            }
        }
    }

    BasicScaffold(topBarTitle = "Home") {
        LazyRow {
            item {
                userList.forEach { user ->
                    Card(
                        modifier = Modifier.padding(4.dp),
                        onClick = {
                            val chatRoomFiltered = viewModel.getRoomFiltered(user.userId.toString(), chatrooms.value)

                            if (currentuser?.username.isNullOrEmpty()) navController.navigate(Screen.LoginScreen)
                            if (!isLoadingChats) {
                                if (chatRoomFiltered == null) {
                                    navController.navigate(Screen.Chat(currentuser?.id.toString()+user.userId, user2 = user.username.toString()))
                                    viewModel.addChatroom(
                                        user2 = user.username.toString(),
                                        user = user,
                                    )
                                } else {
                                    val chatUser2 = if (chatRoomFiltered.user1 != currentuser?.username) chatRoomFiltered.user1 else user.username
                                    chatRoomFiltered.let {
                                        navController.navigate(Screen.Chat(chatId = chatRoomFiltered.name, user2 = chatUser2.toString()))
                                    }
                                }
                            }
                        }
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            ImageCoil(url = user.imageUrl.toString())
                            Text(text = user.username.toString())
                        }
                    }
                }
            }
        }

        Box(modifier = Modifier.fillMaxWidth()) {
            if (isLoadingChats) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            } else {
                LazyRow {
                    items(parentChatrooms.value.filter { channels ->
                        val current = currentuser?.username.toString()
                        /*friendList.any { friendData ->
                            friendData.username == channels.value.user1 && current == channels.value.user2
                                    || friendData.username == channels.value.user2 && current == channels.value.user1
//                            filteredRoom(current, channels.value.user1, channels.value.user2)
                        }*/
                        filteredRoom(current, channels.value.user1, channels.value.user2)
                    }) { channelItemParent ->
                        val chatName = chatRoomName(currentuser?.username.toString(), channelItemParent.value.user1, channelItemParent.value.user2)
                        val friendInstance = friendList.firstOrNull {
                            it.username == chatName
                        }
                        Card(
                            modifier = Modifier.padding(4.dp),
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {

                                val image = friendList.map {
                                    if (it.username == chatName) it.imageUrl
                                }
//                                Log.d("ff", friendInstance.toString())
//                                Log.d("imageff", image.toString())
                                ProfileCard(
                                    name = chatName,
                                    image = friendInstance?.imageUrl.toString(),
                                    onLongClick = {
                                        isPressed.value = true //!isPressed.value
                                        viewModel.deleteChatroom(channelItemParent.key)
                                    },
                                    alertTitle = "Delete Chatroom",
                                    alertButton = "Delete"
                                ) {
                                    currentuser?.let {
                                        viewModel.readMessage(channelItemParent.key, channelItemParent.value, it.username!!)
                                        navController.navigate(
                                            Screen.Chat(
                                                chatId = channelItemParent.value.name,
                                                user2 = chatName,
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
}

fun filteredRoom(current: String, user1: String, user2: String): Boolean {
    return userCheck(current, user1, true) ||
            userCheck(current, user2, true)
}

fun chatRoomName(current: String, user2: String, user3: String = ""): String {
    return if (userCheck(current, user2)) user2 else user3
}

fun userCheck(current: String, user2: String, equal: Boolean = false): Boolean {
    return if (!equal) current != user2 else current == user2
}
package id.neotica.neoverse.presentation.screen.home

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
import id.neotica.neoverse.data.model.ChatRoom
import id.neotica.neoverse.data.model.ParentNeo
import id.neotica.neoverse.data.model.User
import id.neotica.neoverse.data.remote.ApiResult
import id.neotica.neoverse.presentation.navigation.Screen
import id.neotica.neoverse.presentation.ui.components.BasicScaffold
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeView(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: HomeViewModel = koinViewModel()
) {
    val isPressed = remember { mutableStateOf(false) }
    var userList by remember { mutableStateOf<List<User>>(emptyList()) }
    val chatrooms = remember { mutableStateOf<List<ChatRoom>>(emptyList()) }
    val parentChatrooms = remember { mutableStateOf<List<ParentNeo<ChatRoom>>>(emptyList()) }

    var isLoading by remember { mutableStateOf(false) }
    var isLoadingChats by remember { mutableStateOf(false) }
    var currentuser by remember { mutableStateOf<User?>(null) }

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

    LaunchedEffect(key1 = true) {
        if (viewModel.isLoggedIn()) {
            if (!viewModel.isUserVerified()) navController.navigate(Screen.VerifyEmailScreen)
        }
        viewModel.getUser()
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

    LaunchedEffect(key1 = true) {
        viewModel.getChatroom()
        viewModel.chatroom.collect { result ->
            Log.d("chatroomcollect", result.data.toString())
            when(result) {
                is ApiResult.Loading -> {
                    Log.d("chatroomcollect", "loading")

                    isLoadingChats = true
                }
                is ApiResult.Success -> {
                    Log.d("chatroomcollect", "scs")

                    isLoadingChats = false
                    val list = mutableListOf<ChatRoom>()
                    result.data?.forEach { chatResult ->
                        list.add(chatResult.value)
                        parentChatrooms.value = result.data
                        chatrooms.value = list
                    }
                }
                is ApiResult.Error -> {
                    Log.d("chatroomcollect", "error")

                    isLoadingChats = false
                }
            }
        }
    }

    BasicScaffold(topBarTitle = "Home") {
        LazyRow {
            item {
                userList.forEach { user ->
                    /*Card(
                        modifier = Modifier.padding(4.dp),
                        onClick = {
                            val chatRoomFiltered = viewModel.getRoomFiltered(user.userId.toString(), chatrooms.value)

                            if (currentuser?.username.isNullOrEmpty()) navController.navigate(Screen.LoginScreen)
                            if (!isLoadingChats) {
                                if (chatRoomFiltered == null) {
                                    navController.navigate(Screen.Chat(currentuser?.userId.toString()+user.userId, user2 = user.username.toString()))
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
                    }*/
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
                        filteredRoom(current, channels.value.user1, channels.value.user2)
                    }) { channelItemParent ->
                        val chatName = chatRoomName(currentuser?.username.toString(), channelItemParent.value.user1, channelItemParent.value.user2)
                        Card(
                            modifier = Modifier.padding(4.dp),
                            onClick = {
                                currentuser?.let {
                                    viewModel.readMessage(channelItemParent.key, channelItemParent.value, it.username!!)
                                    navController.navigate(Screen.Chat(
                                        chatId = channelItemParent.value.name,
                                        user2 = chatName,
                                        )
                                    )
                                }
                            }
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(chatName)
                            }
                        }
                    }
                }
            }
        }

        viewModel.getUsers()

//        NeoButton("Show user") { navController.navigate(Screen.FriendScreen)}
//        NeoButton("Game") { navController.navigate(RootScreen.GameNav) }

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
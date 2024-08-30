package id.neotica.neoverse.presentation.screen.home

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
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
import id.neotica.neoverse.presentation.navigation.RootScreen
import id.neotica.neoverse.presentation.navigation.Screen
import id.neotica.neoverse.presentation.ui.components.BasicScreen
import id.neotica.neoverse.presentation.ui.components.ImageCoil
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
            when(result) {
                is ApiResult.Loading -> { isLoadingChats = true }
                is ApiResult.Success -> {
                    isLoadingChats = false
                    val list = mutableListOf<ChatRoom>()
                    result.data?.forEach { chatResult ->
                        list.add(chatResult.value)
                    }

                    parentChatrooms.value = result.data!!
                    chatrooms.value = list
                }
                is ApiResult.Error -> {isLoadingChats = false}
            }
        }
    }

    BasicScreen(paddingValues = paddingValues) {
        Box(modifier = Modifier.fillMaxWidth()) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            } else {
                LazyRow(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    item {
                        userList.forEach { user ->
                            Card(
                                modifier = Modifier.padding(4.dp),
                                onClick = {
                                    val chatroom1 = chatrooms.value.find {
                                        val current = currentuser?.userId.toString()+user.userId
                                        userCheck(current, it.user1id+it.user2id, true) ||
                                                userCheck(current, it.user2id+it.user1id, true)
                                    }
                                    val chatRoomName = chatRoomName(currentuser?.username.toString(), chatroom1?.user1.toString(), chatroom1?.user2.toString())
                                    if (currentuser?.username.isNullOrEmpty()) navController.navigate(Screen.LoginScreen)
                                    if (!isLoadingChats) {
                                        val roomName = chatRoomName(currentuser?.username.toString(), user.username.toString())
                                        if (chatroom1 == null) {
                                            navController.navigate(Screen.Chat(currentuser?.userId+user.userId, user2 = user.username.toString()))
                                            viewModel.addChatroom(
                                                user2 = roomName,
                                                user = user,
                                            )
                                        } else {
                                            navController.navigate(Screen.Chat(chatroom1.name, user2 = chatRoomName))
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
            }
        }

        Box(modifier = Modifier.fillMaxWidth()) {
            if (isLoadingChats) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            } else {
                LazyRow(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
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


        Text("homeview")
        viewModel.getUsers()
        Button(onClick = {
            navController.navigate(Screen.FriendScreen)
        }) {
            Text("Show user")
        }
        Button(onClick = {
            navController.navigate(RootScreen.GameNav)
        }) {
            Text("Game")
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
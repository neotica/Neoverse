package id.neotica.neoverse.presentation.screen.friend

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import id.neotica.neoverse.data.model.ChatRoom
import id.neotica.neoverse.data.model.Friends
import id.neotica.neoverse.data.model.User
import id.neotica.neoverse.data.remote.ApiResult
import id.neotica.neoverse.presentation.navigation.RootScreen
import id.neotica.neoverse.presentation.navigation.Screen
import org.koin.androidx.compose.koinViewModel

@Composable
fun FriendView(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: FriendViewModel = koinViewModel()
) {

    val chatrooms = remember { mutableStateOf<List<ChatRoom>>(emptyList()) }
    var friendList by remember { mutableStateOf<List<Friends>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var isLoadingChats by remember { mutableStateOf(false) }
    var currentuser by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(key1 = true) {
        viewModel.getChatroom()
        viewModel.chatroom.collect { result ->
            Log.d("chatroomcollect", result.data .toString())
            when(result) {
                is ApiResult.Loading -> { isLoadingChats = true }
                is ApiResult.Success -> {
                    isLoadingChats = false
                    val list = mutableListOf<ChatRoom>()
                    result.data?.forEach { chatResult ->
                        list.add(chatResult.value)
                    }

                    chatrooms.value = list
                }
                is ApiResult.Error -> {
                    isLoadingChats = false
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
        if (!viewModel.isLoggedIn()) navController.navigate(Screen.LoginScreen) {
            popUpTo(RootScreen.HomeNav) {
                inclusive = true
            }
            launchSingleTop
        }
        viewModel.getFriends()
        viewModel.friendList.collect { result ->
            Log.d("friend", friendList.toString())
            when(result) {
                is ApiResult.Loading -> isLoading = true
                is ApiResult.Success -> {
                    isLoading = false
                    result.data?.let { friendList = result.data }
                }
                is ApiResult.Error -> {
                    isLoading = false
                    Log.d("neotica", result.errorMessage.toString())
                }
            }
        }
    }


    Column(
        Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
        val idValue = rememberSaveable { mutableStateOf("") }

        Text("Friend screen")
        Box {
            Button(onClick = {
                navController.navigate(Screen.SearchUserScreen)
            }) {
                Text("Navigate to search")
            }
        }

        Box {
            LazyColumn {
                item {
                    friendList.forEach { friends ->
                        Card(
                            modifier = Modifier.padding(4.dp),
                            onClick = {
                                val chatRoomFiltered = viewModel.getRoomFiltered(friends.id, chatrooms.value)
                                val currentId = viewModel.currentUserId()

                                if (!isLoadingChats) {
                                    Log.d("friendchat", chatRoomFiltered.toString())
                                    if (chatRoomFiltered.toString() == "null") {
                                        Log.d("friendchat", "null")

                                        navController.navigate(
                                            Screen.Chat(
                                                currentId + friends.id,
                                                user2 = friends.name
                                            )
                                        )
                                        val addUser = User(
                                            userId = friends.id,
                                            username = friends.name
                                        )
                                        viewModel.addChatroom(
                                            user2 =  friends.name,
                                            user = addUser,
                                        )
                                    } else {
                                        Log.d("friendchat", "not null")

                                        val chatUser2 =
                                            if (chatRoomFiltered?.user1 != viewModel.currentUserName()) chatRoomFiltered?.user1 else friends.name
                                        chatRoomFiltered.let {
                                            navController.navigate(
                                                Screen.Chat(
                                                    chatId = chatRoomFiltered?.name.toString(),
                                                    user2 = friends.name
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        ) {
                            Text(
                                modifier = Modifier.padding(4.dp),
                                text = friends.name
                            )
                        }
                    }
                }
            }
        }
    }
}
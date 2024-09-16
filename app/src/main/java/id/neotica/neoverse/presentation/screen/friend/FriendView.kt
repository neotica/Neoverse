package id.neotica.neoverse.presentation.screen.friend

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import id.neotica.neoverse.data.local.entity.FriendEntity
import id.neotica.domain.model.ChatRoom
import id.neotica.domain.model.User
import id.neotica.domain.ApiResult
import id.neotica.neoverse.data.local.Mapper
import id.neotica.neoverse.presentation.navigation.RootScreen
import id.neotica.neoverse.presentation.navigation.Screen
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.LoadingIndicatorBool
import id.neotica.ui.components.NeoButton
import id.neotica.ui.components.ProfileCard
import org.koin.androidx.compose.koinViewModel

@Composable
fun FriendView(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: FriendViewModel = koinViewModel()
) {

    val chatrooms = remember { mutableStateOf<List<ChatRoom>>(emptyList()) }
    var friendList by remember { mutableStateOf<List<FriendEntity>>(emptyList()) }
    var friendList2 by remember { mutableStateOf<List<User>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var isLoadingChats by remember { mutableStateOf(false) }
    var isFriendEmpty by remember { mutableStateOf(false) }

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
        if (!viewModel.isLoggedIn()) navController.navigate(Screen.LoginScreen) {
            popUpTo(RootScreen.HomeNav) {
                inclusive = true
            }
            launchSingleTop
        }
        viewModel.getFriendsRoom().collect {
            if (it.isNotEmpty()) {
                friendList = it
                Log.d("friend", "didnt collect")

            } else {
                isFriendEmpty = true
                viewModel.getFriends()
                Log.d("friend", "else")

            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.friendList.collect { result ->
            Log.d("friend", result.toString())
            when(result) {
                is ApiResult.Loading -> isLoading = true
                is ApiResult.Success -> {
                    isLoading = false
                    result.data?.let {
                        friendList2 = result.data!!
                        viewModel.addFriendList(friendList2)
                    }
                }
                is ApiResult.Error -> {
                    isLoading = false
                    Log.d("neotica", result.errorMessage.toString())
                }
            }
        }
    }


    BasicScaffold(
        "Friends",
        paddingValues = paddingValues
    ) {
        LoadingIndicatorBool(isLoadingChats) {
            Column(
                Modifier
                    .fillMaxSize()
            ) {
                Box {
                    NeoButton("Add Friend") {
                        navController.navigate(Screen.SearchUserScreen)
                    }
                }

                Box {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3)
                    ) {
                        items(friendList) { friends ->
                            val chatRoomFiltered =
                                viewModel.getRoomFiltered(friends.id, chatrooms.value)
                            val currentId = viewModel.currentUserId()
                            ProfileCard(
                                name = friends.username,
                                image = friends.imageUrl,
                                paddingValues = PaddingValues(4.dp),
                                alertTitle = "Delete ${friends.username}?",
                                alertButton = "Delete",
                                onLongClick = {
                                    val mapper = Mapper.friendEntityToResponse(friends)
                                    viewModel.unFriend(mapper)
                                }
                            ) {
                                if (!isLoadingChats) {
                                    Log.d("friendchat", chatRoomFiltered.toString())
                                    if (chatRoomFiltered.toString() == "null") {
                                        Log.d("friendchat", "null")

                                        navController.navigate(
                                            Screen.Chat(
                                                currentId + friends.id,
                                                user2 = friends.username
                                            )
                                        )
                                        val addUser = User(
                                            userId = friends.id,
                                            username = friends.username
                                        )
                                        viewModel.addChatroom(
                                            user2 = friends.username,
                                            user = addUser,
                                        )
                                    } else {
                                        Log.d("friendchat", "not null")

                                        val chatUser2 =
                                            if (chatRoomFiltered?.user1 != viewModel.currentUserName()) chatRoomFiltered?.user1 else friends.username
                                        chatRoomFiltered.let {
                                            navController.navigate(
                                                Screen.Chat(
                                                    chatId = chatRoomFiltered?.name.toString(),
                                                    user2 = friends.username
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
}
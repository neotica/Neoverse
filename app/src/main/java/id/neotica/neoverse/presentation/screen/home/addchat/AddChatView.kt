package id.neotica.neoverse.presentation.screen.home.addchat

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import id.neotica.domain.ApiResult
import id.neotica.domain.model.User
import id.neotica.domain.model.chat.ChatRoom
import id.neotica.domain.utils.randomUUID
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.LoadingIndicatorBool
import id.neotica.ui.components.ProfileCard
import id.neotica.ui.components.droidcore.NeoSearchBar
import id.neotica.ui.navigation.RootScreen
import id.neotica.ui.navigation.Screen
import org.koin.androidx.compose.koinViewModel

@Composable
fun AddChatView(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: AddChatViewModel = koinViewModel()
) {

    var friendList by remember { mutableStateOf<List<User>>(emptyList()) }
    var friendList2 by remember { mutableStateOf<List<User>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var isLoadingChats by remember { mutableStateOf(false) }
    var isFriendEmpty by remember { mutableStateOf(false) }
    var isPressed by remember { mutableStateOf(false) }

    val currentId = viewModel.currentUserId()
    val chatrooms = remember { mutableStateOf<List<ChatRoom>>(emptyList()) }

    val username = rememberSaveable { mutableStateOf("") }

    LaunchedEffect(Unit) {
        if (!viewModel.isLoggedIn()) navController.navigate(Screen.LoginScreen) {
            popUpTo(RootScreen.HomeNav) {
                inclusive = true
            }
            launchSingleTop
        }
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
        viewModel.getChatroom()
        viewModel.chatroom.collect { result ->
            Log.d("chatroomcollect", result.data .toString())
            when(result) {
                is ApiResult.Loading -> {
                    isLoadingChats = true
                }
                is ApiResult.Success -> {
                    isPressed = false
                    isLoadingChats = false
                    val list = mutableListOf<ChatRoom>()
                    result.data?.forEach { chatResult ->
                        list.add(chatResult.value)
                    }
                    chatrooms.value = list
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


    BasicScaffold(
        "Add Chat",
        paddingValues = paddingValues
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            NeoSearchBar(
                value = username,
                label = "Search"
            )
        }
        LoadingIndicatorBool(isLoadingChats) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp)
            ) {
                Box {
                    if (!isLoadingChats) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3)
                        ) {
                            items(
                                if (username.value.isEmpty()) {
                                    friendList
                                } else {
                                    friendList.filter { it.username.contains(username.value, ignoreCase = true) }
                                }
                            ) { friends ->
                                ProfileCard(
                                    name = friends.username,
                                    image = friends.imageUrl,
                                    paddingValues = PaddingValues(4.dp),
                                    alertTitle = "Unfriend ${friends.username}?",
                                    alertButton = "Unfriend",
                                ) {
                                    val friendUsername = friends.username
                                    val friendId = friends.userId

                                    val chatRoomFiltered =
                                        viewModel.getRoomFiltered(friends.userId, chatrooms.value)
                                    if (chatRoomFiltered.toString() == "null") {
                                        Log.d("friendchat", "null")

                                        val roomIdGenerated = randomUUID()

                                        navController.navigate(
                                            Screen.Chat(
                                                chatroomId = roomIdGenerated,
                                                chatId = currentId + friends.userId,
                                                user2 = friendUsername
                                            )
                                        )
                                        val addUser = User(
                                            userId = friendId,
                                            username = friendUsername
                                        )
                                        viewModel.addChatroom(
                                            user2 = friendUsername,
                                            user = addUser,
                                            roomId = roomIdGenerated
                                        )
                                    } else {
                                        Log.d("friendchat", "not null")
                                        chatRoomFiltered.let {
                                            navController.navigate(
                                                Screen.Chat(
                                                    chatroomId = chatRoomFiltered?.id,
                                                    chatId = chatRoomFiltered?.name.toString(),
                                                    user2 = friendUsername
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
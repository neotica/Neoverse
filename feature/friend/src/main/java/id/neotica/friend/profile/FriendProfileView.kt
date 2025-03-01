package id.neotica.friend.profile

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import id.neotica.domain.ApiResult
import id.neotica.domain.model.User
import id.neotica.domain.model.chat.ChatRoom
import id.neotica.domain.utils.randomUUID
import id.neotica.droidcore.component.alert.NeoAlert
import id.neotica.friend.FriendViewModel
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.image.ImageCoil
import id.neotica.ui.components.NeoButton
import id.neotica.ui.navigation.Screen
import org.koin.androidx.compose.koinViewModel

@Composable
fun FriendProfileView(
    navController: NavController,
    viewModel: FriendViewModel = koinViewModel(),
    userId: String,
) {
    val currentId = viewModel.currentUserId()
    val chatrooms = remember { mutableStateOf<List<ChatRoom>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var isLoadingChats by remember { mutableStateOf(false) }
    var friend by remember { mutableStateOf<User?>(null) }
    var isPressed by remember { mutableStateOf(false) }

    val openDialog = remember { mutableStateOf(false) }

    LaunchedEffect(isPressed) {

        viewModel.getFriendProfile(userId)
        viewModel.friendProfile.collect { result ->
            Log.d("profileView", "result $result")
            when(result) {
                is ApiResult.Loading -> {
                    isLoading = true
                }
                is ApiResult.Success -> {
                    isLoading = false
                    isPressed = false
                    friend = result.data
                }
                is ApiResult.Error -> {
                    isLoading = false
                    isPressed = false
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

    if (openDialog.value) {
        NeoAlert(
            openDialog = openDialog,
            title = "Unfriend ${friend?.username.toString()}?",
            backButton = "Cancel",
            confirmButton = {
                NeoButton("Unfriend", negative = true) {
                    friend?.let {
                        viewModel.unFriend(it)
                    }
                    openDialog.value = false
                    viewModel.setBottomSheetState(false)
                }
            }
        )
    }

    BasicScaffold(
        "Profile",
        horizontalAlignment = Alignment.CenterHorizontally,
        topBarVisibility = false,
        navController = navController,
        fullScreen = true
    ) {
        if (isLoading || isLoadingChats) {
            Box(Modifier.fillMaxWidth().height(100.dp).padding(16.dp)) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
        }
        AnimatedVisibility(
            !isLoadingChats,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            LazyColumn {
                item {
                    friend?.let {
                        ImageCoil(
                            it.imageUrl, round = true,
                            size = 100.dp
                        )
                        Column(
                            Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(modifier = Modifier.fillMaxWidth(), text = it.username, textAlign = TextAlign.Center)
                            NeoButton("Chat") {
                                val friendUsername = it.username
                                val friendId = it.userId

                                val chatRoomFiltered =
                                    viewModel.getRoomFiltered(it.userId, chatrooms.value)
                                if (chatRoomFiltered.toString() == "null") {
                                    Log.d("friendchat", "null")

                                    val roomIdGenerated = randomUUID()

                                    navController.navigate(
                                        Screen.Chat(
                                            chatroomId = roomIdGenerated,
                                            chatId = currentId + it.userId,
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
                                    chatRoomFiltered?.let { chatFilteredAbsolute ->
                                        navController.navigate(
                                            Screen.Chat(
                                                chatId = chatFilteredAbsolute.name,
                                                user2 = friendUsername
                                            )
                                        )
                                    }
                                }
                            }

                            NeoButton("Unfriend", negative = true) {
                                openDialog.value = true
                            }
                        }
                    }
                }
            }
        }
    }
}
package id.neotica.neoverse.presentation.screen.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
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
import id.neotica.domain.ApiResult
import id.neotica.domain.model.ChatRoom
import id.neotica.domain.model.ParentNeo
import id.neotica.neoverse.data.local.entity.CurrentUserEntity
import id.neotica.neoverse.data.local.entity.FriendEntity
import id.neotica.neoverse.presentation.navigation.Screen
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.ProfileCard
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeView(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: HomeViewModel = koinViewModel(),
) {
    var isPressed by remember { mutableStateOf(false) }
    val parentChatrooms = remember { mutableStateOf<List<ParentNeo<ChatRoom>>>(emptyList()) }

    var friendList by remember { mutableStateOf<List<FriendEntity>>(emptyList()) }


    var isLoading by remember { mutableStateOf(false) }
    var isLoadingChats by remember { mutableStateOf(false) }
    var currentuser by remember { mutableStateOf<CurrentUserEntity?>(null) }

    LaunchedEffect(isPressed) {
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

    LaunchedEffect(isPressed) {

        viewModel.chatroom.collect { result ->
            when(result) {
                is ApiResult.Loading -> {
                    isLoadingChats = true
                }
                is ApiResult.Success -> {
                    isLoadingChats = false

                    viewModel.chatList.forEach { _ ->
                        parentChatrooms.value = result.data!!
                        viewModel.getFriendsRoom().collect {
                            friendList = it
                        }
                    }
                }
                is ApiResult.Error -> {
                    isLoadingChats = false
                }
            }
        }
    }

    BasicScaffold(topBarTitle = "Home") {

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


                                ProfileCard(
                                    name = chatName,
                                    image = friendInstance?.imageUrl.toString(),
                                    onLongClick = {
                                        isPressed = true
                                        viewModel.deleteChatroom(channelItemParent.value)
                                        },
                                    alertTitle = "Delete Chatroom",
                                    alertButton = "Delete"
                                ) {
                                    currentuser?.let {
                                        viewModel.readMessage(channelItemParent.key, channelItemParent.value, it.username)
                                        navController.navigate(
                                            Screen.Chat(
                                                chatId = channelItemParent.value.name,
                                                user2 = chatName,)
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
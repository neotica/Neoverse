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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import id.neotica.neoverse.data.model.ChatRoom
import id.neotica.neoverse.data.model.ParentChatRoom
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
    val parentChatrooms = remember { mutableStateOf<List<ParentChatRoom>>(emptyList()) }

    var isLoading by remember { mutableStateOf(false) }
    var isLoadingChats by remember { mutableStateOf(false) }
    var currentuser by remember { mutableStateOf<User?>(null) }
    var context = LocalContext.current

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
                                    if (!isLoadingChats) {
                                        viewModel.addChatroom(
                                            user2 = user.username.toString(),
                                            chatrooms = chatrooms.value,
                                            navController = navController
                                        )
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
                        currentuser?.username == channels.value.user1 || currentuser?.username == channels.value.user2

                    }) { channelItemParent ->
                        val channelItem = channelItemParent.value
                        val chatName = if (currentuser?.username != channelItem.user1) {
                            channelItem.user1
                        } else {
                            channelItem.user2
                        }
                        Card(
                            modifier = Modifier.padding(4.dp),
                            onClick = {
                                currentuser?.let {
                                    viewModel.readMessage(channelItemParent.key, channelItemParent.value, it.username!!)
                                    navController.navigate(Screen.Chat(
                                        chatId = channelItem.name,
                                        user2 = chatName,
                                    ))
                                }
                            }
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {

                                if (currentuser?.username != channelItem.user1) {
                                    Text(text = channelItem.user1)
                                } else {
                                    Text(text = channelItem.user2)
                                }
                            }
                        }
                    }
                }
            }
        }


        Text("homeview")
        viewModel.getUsers()
        Button(onClick = {
            isPressed.value = true
            viewModel.getUsers()
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

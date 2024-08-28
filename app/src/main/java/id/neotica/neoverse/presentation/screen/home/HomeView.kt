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
import id.neotica.neoverse.data.model.User
import id.neotica.neoverse.data.remote.ApiResult
import id.neotica.neoverse.presentation.navigation.Screen
import id.neotica.neoverse.presentation.ui.components.BasicScreen
import id.neotica.neoverse.presentation.ui.components.ImageCoil
import id.neotica.neoverse.utils.getPartWithUser
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

    var isLoading by remember { mutableStateOf(false) }
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
                is ApiResult.Loading -> {}
                is ApiResult.Success -> {
                    val list = mutableListOf<ChatRoom>()
                    result.data?.forEach { chatResult ->
                        list.add(chatResult.value)
                    }
                    Log.d("hvcolumn", "name: "+ result.data)

                    chatrooms.value = list
                }
                is ApiResult.Error -> {}
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
                                    viewModel.addChatroom(
                                        user2 = user.username.toString(),
                                        chatrooms = chatrooms.value,
                                        navController = navController
                                        )
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
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            } else {
                LazyRow(modifier = Modifier.fillMaxSize()) {
                    items(//chatrooms.value
                        chatrooms.value.filter { channels ->
                            currentuser?.let { user1 ->
                            getPartWithUser(channels.name, user1.username.toString()) != "No match found"
                            } ?: false
                        }
                    ) { channelItem ->
                        Card(
                            modifier = Modifier.padding(4.dp),
                            onClick = {
                                viewModel.gotoChatRoom(channelItem.name, navController)
                            }
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                val channelParse = viewModel.chatroomName(channelItem.name)
                                Log.d("hvcolumn", channelParse)

                                Text(text = channelParse)
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
            navController.navigate(Screen.Chat("test"))
        }) {
            Text("Chat")
        }
    }
}

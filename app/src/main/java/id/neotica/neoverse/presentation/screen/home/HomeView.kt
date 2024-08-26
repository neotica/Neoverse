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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import id.neotica.neoverse.data.model.User
import id.neotica.neoverse.data.remote.ApiResult
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
    val chatrooms = viewModel.chatroom.collectAsState()
    var isLoading by remember { mutableStateOf(false) }
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
        viewModel.getChatroom()
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
                                    currentuser?.let { user2 ->
                                        val firstUser = user2.username.toString()
                                        val secondUser = user.username.toString() // Assuming 'user' is passed in the composable
                                        val combinedRoomName1 = "1:$firstUser and 2:$secondUser"
                                        val combinedRoomName2 = "1:$secondUser and 2:$firstUser"

                                        val existingRoom = chatrooms.value.find { it.name == combinedRoomName1 || it.name == combinedRoomName2 }

                                        if (existingRoom != null) {
                                            Log.d("neotica", "navigate")
                                            navController.navigate(Screen.Chat(existingRoom.name))
                                        } else {
                                            Log.d("neotica", "addroom")
                                            viewModel.addChatroom(combinedRoomName1, viewModel.currentUser())
                                            navController.navigate(Screen.Chat(combinedRoomName1))
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
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            } else {
                LazyRow(modifier = Modifier.fillMaxSize()) {
                    items(chatrooms.value.filter { channels ->
                        currentuser?.let { user1 ->
                            getPartWithUser(channels.name, user1.username.toString()) != "No match found"
                        } ?: false
                    }) { channelItem ->
                        Card(
                            modifier = Modifier.padding(4.dp),
                            onClick = {
                                currentuser?.let { user1 ->
                                    val exactMatch = getPartWithUser(channelItem.name, user1.username.toString())
                                    if (exactMatch != "No match found") {
                                        navController.navigate(Screen.Chat(chatId = channelItem.name))
                                    }
                                }
                            }
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                currentuser?.let { user1 ->
                                    val exactMatch = getPartWithUser(channelItem.name, user1.username.toString())
                                    val channelParse = channelItem.name.replace(exactMatch, "").substringAfter(":").replace("and", "").trim()

                                    if (exactMatch != "No match found") {
                                        Text(text = channelParse)
                                    }
                                }
                            }
                        }
                    }
                }
                /*LazyRow(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(chatrooms.value) { channels ->
                        Card(
                            modifier = Modifier.padding(4.dp),
                            onClick = {
                                if (currentuser != null) {
                                    navController.navigate(Screen.Chat(chatId = channels.name))
                                }
                            }
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                currentuser?.let { user1 ->
                                    val now = getPartWithUser(channels.name, user1.username.toString())
                                    Text(text = now)

                                }
                            }
                        }
                    }
                }*/
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

fun getPartWithUser(combinedRoomName: String, current: String): String {
    // Split the combined room name by " and "
    val parts = combinedRoomName.split(" and ")

    // Find the part that contains the secondUser
    return parts.find { it.contains(current) && it.length == current.length + 2 } ?: "No match found"
}
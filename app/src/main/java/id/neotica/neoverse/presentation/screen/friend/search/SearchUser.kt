package id.neotica.neoverse.presentation.screen.friend.search

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
import id.neotica.droidcore.component.textfield.NeoTextField
import id.neotica.neoverse.data.model.User
import id.neotica.neoverse.data.remote.ApiResult
import id.neotica.neoverse.presentation.navigation.RootScreen
import id.neotica.neoverse.presentation.navigation.Screen
import id.neotica.neoverse.presentation.screen.friend.FriendViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchUser(
    navController: NavController, paddingValues: PaddingValues,
    viewModel: FriendViewModel = koinViewModel()
) {
    val username = rememberSaveable { mutableStateOf("") }
    var userList by remember { mutableStateOf<List<User>>(emptyList()) }
    val isPressed = remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(isPressed.value) {
        viewModel.userList.collect { result ->
            when(result) {
                is ApiResult.Loading -> isLoading = true
                is ApiResult.Success -> {
                    isLoading = false
                    result.data?.let { userList = result.data }
                }
                is ApiResult.Error -> {
                    isLoading = false
                    Log.d("neotica", result.errorMessage.toString())
                }
            }
        }
    }

    Box(
        Modifier
            .padding(paddingValues)
            .fillMaxSize()) {
        Column(Modifier.padding(horizontal = 8.dp)) {
            Text("Search User")
            NeoTextField(value = username)
            Button(onClick = {
                isPressed.value = true
                viewModel.searchFriend(username.value)
            }) {
                Text("Search")
            }
            Box(Modifier.weight(1f)) {
                LazyColumn {
                    item {
                        if (userList.isEmpty()) {
                            Text("It's empty here ^^")
                        } else {
                            userList.forEach { user ->
                                Card(
                                    onClick = {
                                        val friendId = user.userId.toString()
                                        if (friendId != viewModel.currentUserId()) {
                                            navController.navigate(Screen.FriendProfileScreen(friendId))
                                        } else {
                                            navController.navigate(RootScreen.AuthNav)
                                        }
                                    }
                                ) {
                                    Text(user.username.toString())
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
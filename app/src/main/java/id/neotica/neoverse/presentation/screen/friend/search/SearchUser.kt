package id.neotica.neoverse.presentation.screen.friend.search

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
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
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import id.neotica.droidcore.component.textfield.NeoTextField
import id.neotica.domain.model.User
import id.neotica.domain.ApiResult
import id.neotica.neoverse.presentation.navigation.RootScreen
import id.neotica.neoverse.presentation.screen.friend.FriendViewModel
import id.neotica.neoverse.presentation.ui.components.BasicScaffold
import id.neotica.neoverse.presentation.ui.components.NeoButton
import id.neotica.neoverse.presentation.ui.components.ProfileCard
import kotlinx.coroutines.launch
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
                    result.data?.let { userList = result.data!! }
                }
                is ApiResult.Error -> {
                    isLoading = false
                    Log.d("neotica", result.errorMessage.toString())
                }
            }
        }
    }

    BasicScaffold(
        topBarTitle = "Search User",
        true,
        navController = navController,
        paddingValues = paddingValues,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center
                    ) {
                        item {
                            if (userList.isEmpty()) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("It's empty here ^^")
                                }
                            } else {
                                userList.forEach { user ->
                                    Column(
                                        Modifier.fillMaxSize().padding(horizontal = 20.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                    ) {
                                        ProfileCard(
                                            name = user.username,
                                            image = user.imageUrl,
                                            size = 200.dp,
                                            content = {
                                                NeoButton(
                                                    text = "Add Friend",
                                                    maxWidth = true
                                                ) {
                                                    val friends = User(
                                                        userId = user.userId,
                                                        username = user.username,
                                                        userEmail = user.userEmail,
                                                        status = user.status,
                                                        imageUrl = user.imageUrl
                                                    )

                                                    if (friends.userId != viewModel.currentUserId()) {
                                                        viewModel.viewModelScope.launch {
                                                            viewModel.addFriend(friends)
                                                        }
                                                    } else {
                                                        navController.navigate(RootScreen.AuthNav)
                                                    }
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                Column(
                    Modifier.padding(horizontal = 4.dp)
                ) {
                    NeoTextField(
                        value = username,
                        onValueChange = {
                            viewModel.searchFriend(username.value.trim())
                        }
                    )
                    NeoButton(
                        text = "Search",
                        maxWidth = true
                    ) {
                        isPressed.value = true
                        viewModel.searchFriend(username.value)
                    }
                }
            }
        }
    }
}
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import id.neotica.domain.model.User
import id.neotica.domain.ApiResult
import id.neotica.ui.navigation.RootScreen
import id.neotica.neoverse.presentation.screen.friend.FriendViewModel
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.NeoButton
import id.neotica.ui.components.ProfileCard
import id.neotica.ui.components.droidcore.NeoSearchBar
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchUser(
    navController: NavController,
    viewModel: FriendViewModel = koinViewModel()
) {
    val username = rememberSaveable { mutableStateOf("") }
    var userList by remember { mutableStateOf<List<User>>(emptyList()) }
    var friendList by remember { mutableStateOf<List<User>>(emptyList()) }
    val isPressed = remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }

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

    LaunchedEffect(isPressed.value) {
        viewModel.getFriendsRoom().collect { result ->
            friendList = result
        }
    }

    BasicScaffold(
        topBarTitle = "Add Friend",
        true,
        navController = navController,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            Column {
                NeoSearchBar(
                    value = username,
                    label = "Search",
                    focusRequester = focusRequester,
                    padding = PaddingValues(horizontal = 16.dp),
                    onValueChange = {
                        viewModel.searchFriend(username.value.trim())
                    }
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    LazyColumn(
                        verticalArrangement = Arrangement.Center
                    ) {
                        item {
                            when {
                                userList.isEmpty() -> {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("It's empty here ^^")
                                    }
                                }
                                else -> {
                                    userList.forEach { user ->
                                        Column(
                                            Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                        ) {
                                            ProfileCard(
                                                name = user.username,
                                                image = user.imageUrl,
                                                size = 100.dp,
                                                content = {
                                                    val friendValue = friendList.any { it.userId == user.userId }

                                                    val textValue = when {
                                                        user.userId == viewModel.currentUserId() -> "Go to your profile"
                                                        friendValue -> "Friend Added"
                                                        else -> "Add Friend"
                                                    }

                                                    NeoButton(
                                                        text = textValue,
                                                        maxWidth = true,
                                                        enabled = !friendValue
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
                                                            navController.navigateUp()
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
                }
            }
        }
    }
}
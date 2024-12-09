package id.neotica.neoverse.presentation.screen.friend

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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import id.neotica.domain.ApiResult
import id.neotica.domain.model.User
import id.neotica.droidcore.component.textfield.NeoTextField
import id.neotica.ui.R
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.LoadingIndicatorBool
import id.neotica.ui.components.ProfileCard
import id.neotica.ui.navigation.RootScreen
import id.neotica.ui.navigation.Screen
import org.koin.androidx.compose.koinViewModel

@Composable
fun FriendView(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: FriendViewModel = koinViewModel()
) {

    var friendList by remember { mutableStateOf<List<User>>(emptyList()) }
    var friendList2 by remember { mutableStateOf<List<User>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    val isLoadingChats by remember { mutableStateOf(false) }
    var isPressed by remember { mutableStateOf(false) }

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
                    isPressed = false
                    viewModel.getFriends()
                    Log.d("friend", "else")

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
        "Friends",
        paddingValues = paddingValues
    ) {
        Box(
            Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
        ) {
            Row (
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                NeoTextField(
                    value = username,
                    label = "Search",
                    trailingIcon = {
                        IconButton (
                            modifier = Modifier.padding(0.dp),
                            onClick = {
                                navController.navigate(Screen.SearchUserScreen)
                            },
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_add_friend),
                                contentDescription = "",
                                tint = White
                            )
                        }
                    },
                )
            }
        }
        LoadingIndicatorBool(isLoadingChats) {
            Column(
                Modifier
                    .fillMaxSize()
            ) {
                Box(Modifier.padding(horizontal = 12.dp, vertical = 16.dp)) {
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
                                navController.navigate(Screen.FriendProfileScreen(
                                    userId = friends.userId
                                ))
                            }
                        }
                    }
                }
            }
        }
    }
}
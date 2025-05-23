package id.neotica.friend

import android.util.Log
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import id.neotica.domain.ApiResult
import id.neotica.domain.model.User
import id.neotica.friend.profile.FriendProfileView
import id.neotica.ui.R
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.LoadingIndicatorBool
import id.neotica.ui.components.NeoButton
import id.neotica.ui.components.ProfileCard
import id.neotica.ui.components.droidcore.NeoSearchBar
import id.neotica.ui.navigation.RootScreen
import id.neotica.ui.navigation.Screen
import id.neotica.ui.theme.DarkBackground
import id.neotica.ui.theme.DarkPrimaryTransparent80
import id.neotica.ui.theme.NeoPadding
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
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
    var friendId by remember { mutableStateOf("") }

    var showCard by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState()
    var showFriendProfileSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(500L)
        showCard = true
    }

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

    if (viewModel.sheetState.collectAsState().value) {
        ModalBottomSheet(
            onDismissRequest = {
                showFriendProfileSheet = false
                viewModel.setBottomSheetState(false)
                friendId = ""
                viewModel.makeFriendNull()
            },
            sheetState = sheetState,
            containerColor = DarkBackground
        ) {
            FriendProfileView(
                navController, userId = friendId, //sheetState = showFriendProfileSheet
            )
        }
    }

    BasicScaffold(
        "Friends",
        fullScreen = true
    ) {
        Box(
            Modifier
                .padding(
                    top = NeoPadding.topBarPadding,
                    start = 16.dp,
                    end = 16.dp
                )
                .fillMaxWidth()
        ) {
            Row (
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                NeoSearchBar(
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
                Box(
                    Modifier.padding(
                        top = 16.dp,
                        start = 12.dp,
                        end = 12.dp
                    )
                ) {

                    if(friendList.isEmpty()) {
                        androidx.compose.animation.AnimatedVisibility(
                            visible = friendList.isEmpty() && showCard,
                            enter = fadeIn(tween(500))
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Card(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .align(Alignment.Center),
                                    colors = CardDefaults.cardColors().copy(containerColor = DarkPrimaryTransparent80)
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            modifier = Modifier.padding(8.dp),
                                            text = "You've got no friends! try adding some. 😣",
                                            textAlign = TextAlign.Center
                                        )
                                        NeoButton("Add friends", modifier = Modifier.padding(8.dp)) {navController.navigate(Screen.SearchUserScreen)}
                                    }
                                }
                            }
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            contentPadding = PaddingValues(bottom = NeoPadding.systemBarPadding + paddingValues.calculateBottomPadding())
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
                                    friendId = friends.userId
                                    showFriendProfileSheet = true
                                    viewModel.setBottomSheetState(true)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
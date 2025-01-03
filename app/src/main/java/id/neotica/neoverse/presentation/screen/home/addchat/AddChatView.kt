package id.neotica.neoverse.presentation.screen.home.addchat

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import id.neotica.domain.ApiResult
import id.neotica.domain.model.User
import id.neotica.domain.model.chat.ChatRoom
import id.neotica.domain.utils.randomUUID
import id.neotica.neoverse.presentation.screen.friend.FriendViewModel
import id.neotica.neoverse.presentation.screen.friend.profile.FriendProfileView
import id.neotica.ui.R
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.NeoButton
import id.neotica.ui.components.ProfileCard
import id.neotica.ui.components.droidcore.NeoSearchBar
import id.neotica.ui.navigation.RootScreen
import id.neotica.ui.navigation.Screen
import id.neotica.ui.theme.DarkBackground
import id.neotica.ui.theme.DarkPrimaryTransparent80
import id.neotica.world.feat.card.RavenCard
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun AddChatView(
    navController: NavController,
    viewModel: AddChatViewModel = koinViewModel(),
    friendViewModel: FriendViewModel = koinViewModel()
) {

    var friendList by remember { mutableStateOf<List<User>>(emptyList()) }
    var friendList2 by remember { mutableStateOf<List<User>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var isLoadingChats by remember { mutableStateOf(false) }
    var isFriendEmpty by remember { mutableStateOf(false) }
    var isPressed by remember { mutableStateOf(false) }

    val currentId = viewModel.currentUserId()
    val chatrooms = remember { mutableStateOf<List<ChatRoom>>(emptyList()) }

    val username = rememberSaveable { mutableStateOf("") }
    var friendId by remember { mutableStateOf("") }

    if (friendViewModel.sheetState.collectAsState().value) {
        Dialog (
            onDismissRequest = {
                friendViewModel.setBottomSheetState(false)
                friendId = ""
                friendViewModel.makeFriendNull()
            }
        ) {
            Card(
                colors = CardDefaults.cardColors().copy(containerColor = DarkBackground),
                modifier = Modifier
                    .height(300.dp)
                    .padding(16.dp)
            ) {
                Box(Modifier.padding(16.dp)) {
                    FriendProfileView(
                        navController, userId = friendId
                    )
                }
            }
        }
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
                    isFriendEmpty = true
                    isPressed = false
                    viewModel.getFriends()
                    Log.d("friend", "else")

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
        "Add Chat",
        topBarVisibility = false,
        fullScreen = true
    ) {
        Box {
            Column {
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
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
                androidx.compose.animation.AnimatedVisibility(username.value.isEmpty()) {
                    RavenCard(navController)
                }
                if (isLoadingChats) {
                    Box(Modifier.fillMaxWidth().padding(16.dp)) {
                        CircularProgressIndicator(Modifier.align(Alignment.Center))
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = !isLoadingChats,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp)
            ) {
                var showCard by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    delay(500)
                    showCard = true
                }

                Box {
                    if (friendList.isEmpty()) androidx.compose.animation.AnimatedVisibility(
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
                                    .align(Alignment.TopCenter),
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
                    if (!isLoadingChats) {
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
                                val haptic = LocalHapticFeedback.current
                                ProfileCard(
                                    name = friends.username,
                                    image = friends.imageUrl,
                                    paddingValues = PaddingValues(4.dp),
                                    alertTitle = "Unfriend ${friends.username}?",
                                    alertButton = "Unfriend",
                                    onLongClick = {
                                        friendViewModel.setBottomSheetState(true)
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        friendId = friends.userId
                                    }
                                ) {
                                    val friendUsername = friends.username
                                    friendId = friends.userId

                                    val chatRoomFiltered =
                                        viewModel.getRoomFiltered(friends.userId, chatrooms.value)
                                    if (chatRoomFiltered.toString() == "null") {
                                        Log.d("friendchat", "null")

                                        val roomIdGenerated = randomUUID()

                                        navController.navigate(
                                            Screen.Chat(
                                                chatroomId = roomIdGenerated,
                                                chatId = currentId + friends.userId,
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
                                        chatRoomFiltered.let {
                                            navController.navigate(
                                                Screen.Chat(
                                                    chatroomId = chatRoomFiltered?.id,
                                                    chatId = chatRoomFiltered?.name.toString(),
                                                    user2 = friendUsername
                                                )
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
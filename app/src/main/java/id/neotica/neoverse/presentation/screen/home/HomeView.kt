package id.neotica.neoverse.presentation.screen.home

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.pm.PackageInfoCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import id.neotica.domain.ApiResult
import id.neotica.domain.model.User
import id.neotica.domain.model.chat.ChatRoom
import id.neotica.domain.model.chat.ParentNeo
import id.neotica.domain.utils.convertLongToTime
import id.neotica.domain.utils.filteredRoom
import id.neotica.domain.utils.returnFriendName
import id.neotica.neoverse.presentation.screen.home.addchat.AddChatView
import id.neotica.neoverse.utils.buddhaQuotes
import id.neotica.neoverse.utils.marcusAureliusQuotes
import id.neotica.ui.R
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.ChatListItem
import id.neotica.ui.components.FlipCard
import id.neotica.ui.components.ImageCoil
import id.neotica.ui.components.NeoFAB
import id.neotica.ui.components.animation.LoadingText
import id.neotica.ui.components.animation.NeoRefreshBox
import id.neotica.ui.components.droidcore.NeoSearchBar
import id.neotica.ui.components.scroll.nestedScrollConnection
import id.neotica.ui.components.simpleHaptic
import id.neotica.ui.navigation.RootScreen
import id.neotica.ui.navigation.Screen
import id.neotica.ui.navigation.WorldScreen
import id.neotica.ui.theme.DarkBackground
import id.neotica.ui.theme.FontSize
import id.neotica.ui.theme.NeoPadding
import id.neotica.ui.theme.Transparent
import id.neotica.ui.theme.TransparentText20
import id.neotica.ui.theme.TransparentText40
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeView(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: HomeViewModel = koinViewModel(),
) {
    var isPressed by remember { mutableStateOf(false) }
    val parentChatrooms = remember { mutableStateOf<List<ParentNeo<ChatRoom>>>(emptyList()) }

    var friendList by remember { mutableStateOf<List<User>>(emptyList()) }
    var friendListLocal by remember { mutableStateOf<List<User>>(emptyList()) }

    var isLoading by remember { mutableStateOf(false) }
    var isLoadingChats by remember { mutableStateOf(false) }
    var currentuser by remember { mutableStateOf<User?>(null) }
    val lifecycleOwner = LocalLifecycleOwner.current
    var currentAura by remember { mutableStateOf<Long?>(null) }

    val headerAlpha2 = remember { mutableFloatStateOf(1f) }
    val headerSize2 = remember { mutableStateOf(100.dp) }

    var versionCode by remember { mutableStateOf<Int?>(null) }
    val username = rememberSaveable { mutableStateOf("") }

    val context = LocalContext.current

    val sheetState = rememberModalBottomSheetState()
    var showFriendListSheet by remember { mutableStateOf(false) }

    var isChatEmpty by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {

        viewModel.versionCode.collect { result ->
            when(result) {
                is ApiResult.Loading -> {
                    isLoading = true
                }
                is ApiResult.Success -> {
                    isLoading = false
                    versionCode = result.data
                    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                    val version: Long = PackageInfoCompat.getLongVersionCode(packageInfo)
                    versionCode?.let {
                        if (it > version) {
                            navController.navigate(Screen.UpdateAppScreen) {
                                popUpTo(RootScreen.HomeNav) {
                                    inclusive = true
                                }
                                launchSingleTop
                            }
                        }
                    }
                }
                is ApiResult.Error -> {
                    isLoading = false
                }
            }
        }
    }

    LaunchedEffect(isPressed) {
        isPressed = false
        if (viewModel.isLoggedIn()) {
            if (!viewModel.isUserVerified()) navController.navigate(Screen.VerifyEmailScreen) {
                popUpTo(RootScreen.HomeNav) {
                    inclusive = true
                }
                launchSingleTop
            }
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
                    isPressed = false

                    viewModel.chatList.forEach { _ ->
                        parentChatrooms.value = result.data!!
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
                is ApiResult.Error -> {
                    isPressed = false
                    isLoadingChats = false
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.fetchAuras()
        viewModel.totalAuras.collect { result ->
            when(result) {
                is ApiResult.Loading -> {}
                is ApiResult.Success -> {
                    currentAura = result.data
                }
                is ApiResult.Error -> {}
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
                        friendListLocal = result.data!!
                        viewModel.addFriendList(friendListLocal)
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

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.fetchAuras()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val nestedState = rememberNestedScrollInteropConnection()
    val listState = rememberLazyListState()
    val haptic = LocalHapticFeedback.current

    if (showFriendListSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFriendListSheet = false },
            sheetState = sheetState,
            containerColor = DarkBackground
        ) {
            AddChatView(navController)
        }
    }

    BasicScaffold (
        topBarTitle = "HomeView",
        topBarVisibility = false,
        fullScreen = true
    ) {
        Spacer(Modifier.padding(top = NeoPadding.statusBarPadding + 16.dp))
        Column {
            Column {
                Column(
                    modifier = Modifier
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                        )
                        .fillMaxWidth(),
                ) {
                    Row {
                        Column(
                            Modifier.weight(4f)
                        ) {
                            Text(
                                text = "Welcome, ${currentuser?.username ?: ""}!",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontSize = FontSize.Big,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "You have ${currentAura ?: "0"} Auras.",
                                fontSize = FontSize.Small,
                                color = TransparentText20
                            )
                        }
                        Column(
                            Modifier
                                .align(Alignment.CenterVertically)
                        ) {
                            currentuser?.let {
                                Box(Modifier.size(55.dp)) {
                                    FlipCard(
                                        frontSide = {
                                            Box {
                                                Card(
                                                    modifier = Modifier.size(55.dp),
                                                    shape = CircleShape,
                                                    border = BorderStroke(1.dp, TransparentText40),
                                                    colors = CardDefaults.cardColors().copy(containerColor = Transparent)
                                                ) {
                                                    Column(
                                                        Modifier.size(55.dp),
                                                        verticalArrangement = Arrangement.Center,
                                                        horizontalAlignment = Alignment.CenterHorizontally
                                                    ) {
                                                        ImageCoil(
                                                            url = it.imageUrl,
                                                            round = true,
                                                            size = 53.dp,
                                                            contentAlignment = Alignment.Center,
                                                            onLongClick = {
                                                                simpleHaptic(haptic)
                                                                Toast
                                                                    .makeText(context, "Go to Profile", Toast.LENGTH_SHORT)
                                                                    .show()
                                                            }
                                                        ) {
                                                            navController.navigate(RootScreen.AuthNav)
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    ) {
                                        Card(
                                            modifier = Modifier.size(55.dp),
                                            shape = CircleShape,
                                            border = BorderStroke(1.dp, TransparentText40),
                                            colors = CardDefaults.cardColors().copy(containerColor = Transparent)
                                        ) {
                                            Column(
                                                Modifier.size(55.dp),
                                                verticalArrangement = Arrangement.Center,
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                ImageCoil(
                                                    url = it.imageUrl,
                                                    round = true,
                                                    size = 53.dp,
                                                    contentAlignment = Alignment.Center,
                                                    onLongClick = {
                                                        simpleHaptic(haptic)
                                                        Toast
                                                            .makeText(context, "Go to Profile", Toast.LENGTH_SHORT)
                                                            .show()
                                                    }
                                                ) {
                                                    navController.navigate(RootScreen.AuthNav)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                NeoSearchBar(
                    value = username,
                    onValueChange = {
                        if (username.value == "/terminal") {
                            username.value = ""
                            navController.navigate(WorldScreen.TerminalScreen)
                        }
                    },
                    label = "Search Chat",
                    padding = PaddingValues(
                        top = 8.dp,
                        start = 16.dp,
                        end = 16.dp
                    ),
                    trailingIcon = {
                        if (username.value.isNotEmpty()) {
                            IconButton(onClick = { username.value = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear text",
                                )
                            }
                        }
                    }
                )
                Spacer(Modifier.padding(4.dp))
            }
            NeoRefreshBox(
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(nestedState),
                isRefreshing = isLoadingChats,
                onRefresh = {
                    isPressed = true
                    viewModel.getChatroom()
                }
            ) {
                LazyColumn(
                    modifier = Modifier.nestedScroll(
                        nestedScrollConnection(
                            headerAlpha = headerAlpha2,
                            headerSize2
                        )
                    ),
                    state = listState,
                    contentPadding = PaddingValues(
                        bottom = NeoPadding.systemBarPadding + paddingValues.calculateBottomPadding()
                    )
                ) {
//                    item { for(i in 1..19) Text("hshjkcvhbxdbjkcvbcvxcvxdvx") }
                    val chatItem = parentChatrooms.value.filter { channels ->
                        val current = currentuser?.username.toString()
                        filteredRoom(
                            current,
                            channels.value.user1,
                            channels.value.user2
                        )
                    }
                    if(!isLoadingChats) {
                        val friendFilter = chatItem.filter {
                            it.value.user1.contains(username.value, true)
                                    || it.value.user2.contains(username.value)
                        }
                        isChatEmpty = friendFilter.isEmpty()
                        if (friendFilter.isEmpty()) {
                            item {
                                LoadingText(listOf("It's so lonely! Try chatting with some friends."), false)
                            }
                        }
                        items(
                            if(username.value.isEmpty()) {
                                chatItem
                            } else {
                                friendFilter
                            }
                        ) { channelItemParent ->
                            val chatName = returnFriendName(
                                currentuser?.username.toString(),
                                channelItemParent.value.user1,
                                channelItemParent.value.user2
                            )
                            val friendInstance = friendList.firstOrNull {
                                it.username == chatName
                            }
                            ChatListItem(
                                image = friendInstance?.imageUrl.toString(),
                                enableLongClick = true,
                                onLongClick = {},
                                content = {
                                    channelItemParent.value.let {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                modifier = Modifier.weight(5f),
                                                text = chatName,
                                                overflow = TextOverflow.Ellipsis,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = FontSize.Normal
                                            )
                                            Box(
                                                modifier = Modifier.weight(1f),
                                            ) {
                                                Text(
                                                    modifier = Modifier.align(Alignment.CenterEnd),
                                                    text = convertLongToTime(it.lastOpened, true),
                                                    maxLines = 1,
                                                    fontSize = FontSize.Smaller,
                                                    color = TransparentText20
                                                )
                                            }
                                        }
                                        if (it.lastMessage.isNotEmpty()) {
                                            Text(
                                                text = channelItemParent.value.lastMessage,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                fontSize = FontSize.Normal,
                                                color = TransparentText20
                                            )
                                        } else {
                                            Text(
                                                text = "No message has been sent.",
                                                fontStyle = FontStyle.Italic
                                            )
                                        }
                                    }
                                }
                            ) {
                                currentuser?.let {
                                    navController.navigate(
                                        Screen.Chat(
                                            chatroomId = channelItemParent.value.id,
                                            chatId = channelItemParent.value.name,
                                            user2 = chatName
                                        )
                                    )
                                }
                            }
                        }
                    } else {
                        item {
                            LoadingText(buddhaQuotes + marcusAureliusQuotes)
                        }
                    }
                }
            }
        }
    }
    Box(
        Modifier
            .fillMaxSize()
            .padding(PaddingValues(bottom = NeoPadding.systemBarPadding + paddingValues.calculateBottomPadding()))
    ) {
        var isFabLongPressed by remember { mutableStateOf(false) }

        NeoFAB(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp)
                .animateContentSize(),
            onClick = {
                showFriendListSheet = true
            },
            onLongClick = {
                simpleHaptic(haptic)
                isFabLongPressed = true
            }
        ) {
            Row(Modifier.padding(horizontal = 16.dp)) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.add_chat),
                    contentDescription = "",
                    tint = White
                )
                var isFabExtended by remember { mutableStateOf(false) }

                LaunchedEffect(isFabLongPressed) {
                    if (isFabLongPressed) {
                        isFabExtended = true
                        isFabLongPressed = false
                    } else {
                        delay(1000)
                        isFabExtended = false
                    }
                }
                if (isChatEmpty && !isLoadingChats || isFabExtended) Text(text = "Add new chat", Modifier.padding(start= 8.dp))
            }
        }
    }
}
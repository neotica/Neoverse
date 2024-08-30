package id.neotica.neoverse.presentation.screen.friend

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
import id.neotica.neoverse.data.model.Friends
import id.neotica.neoverse.data.remote.ApiResult
import id.neotica.neoverse.presentation.navigation.Screen
import org.koin.androidx.compose.koinViewModel

@Composable
fun FriendView(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: FriendViewModel = koinViewModel()
) {

    var friendList by remember { mutableStateOf<List<Friends>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        viewModel.getFriends()
        viewModel.friendList.collect { result ->
            Log.d("friend", friendList.toString())
            when(result) {
                is ApiResult.Loading -> isLoading = true
                is ApiResult.Success -> {
                    isLoading = false
                    result.data?.let { friendList = result.data }
                }
                is ApiResult.Error -> {
                    isLoading = false
                    Log.d("neotica", result.errorMessage.toString())
                }
            }
        }
    }


    Column(Modifier.fillMaxSize().padding(paddingValues)) {
        val idValue = rememberSaveable { mutableStateOf("") }

        Text("Friend screen")
        Box {
            Button(onClick = {
                navController.navigate(Screen.SearchUserScreen)
            }) {
                Text("Navigate to search")
            }
        }

        Box {
            LazyColumn {
                item {
                    friendList.forEach { friends ->
                        Card(
                            modifier = Modifier.padding(4.dp),
                            onClick = {
//                            navController.navigate(Screen.Chat(chatId = ))
                            }
                        ) {
                            Text(
                                modifier = Modifier.padding(4.dp),
                                text = friends.name
                            )
                        }
                    }
                }
            }
        }
    }
}
package id.neotica.neoverse.presentation.screen.friend.profile

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import id.neotica.neoverse.data.model.Friends
import id.neotica.neoverse.data.model.User
import id.neotica.neoverse.data.remote.ApiResult
import id.neotica.neoverse.presentation.screen.friend.FriendViewModel
import id.neotica.neoverse.presentation.ui.components.BasicScreen
import id.neotica.neoverse.presentation.ui.components.ImageCoil
import id.neotica.neoverse.presentation.ui.components.LoadingIndicatorBool
import org.koin.androidx.compose.koinViewModel

@Composable
fun FriendProfileView(
    paddingValues: PaddingValues,
    userId: String,
    viewModel: FriendViewModel = koinViewModel()
) {
    var user by remember { mutableStateOf<User?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        viewModel.getUser(userId)
        viewModel.user.collect { result ->
            when(result) {
                is ApiResult.Loading -> isLoading = true
                is ApiResult.Success -> {
                    isLoading = false
                    user = result.data
                }
                is ApiResult.Error -> {
                    isLoading = false
                }
            }
        }
    }
    BasicScreen(paddingValues = paddingValues, horizontalAlign = Alignment.CenterHorizontally) {
        LoadingIndicatorBool(isLoading = isLoading) {
            ImageCoil(user?.imageUrl.toString())
            Text(text = user?.username.toString())
            val friends = Friends(
                userId,
                user?.username.toString(),
                user?.imageUrl.toString()
            )
            Button(onClick = { viewModel.addFriend(friends) }) {
                Text("Add Friend!")
            }
        }
    }
}
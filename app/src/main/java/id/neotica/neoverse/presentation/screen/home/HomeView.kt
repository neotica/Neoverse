package id.neotica.neoverse.presentation.screen.home

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
    var isLoading by remember { mutableStateOf(false) }

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
                                modifier = Modifier.padding(4.dp)
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

        Text("homeview")
        viewModel.getUsers()
        Button(onClick = {
            isPressed.value = true
            viewModel.getUsers()
        }) {
            Text("Show user")
        }
        Button(onClick = { navController.navigate(Screen.Chat) }) {
            Text("Chat")
        }
    }
}
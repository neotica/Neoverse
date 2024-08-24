package id.neotica.neoverse.presentation.screen.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import id.neotica.neoverse.data.model.User
import id.neotica.neoverse.data.remote.ApiResult
import id.neotica.neoverse.presentation.navigation.Screen
import org.koin.androidx.compose.koinViewModel

private lateinit var auth: FirebaseAuth

@Composable
fun HomeView(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: HomeViewModel = koinViewModel()
) {
    val isPressed = remember { mutableStateOf(false) }
    var userList by remember { mutableStateOf<List<User>>(emptyList()) }

    LaunchedEffect(isPressed.value) {
        viewModel.userList.collect { result ->
            when(result) {
                is ApiResult.Loading -> Log.d("neotica", "Loading")
                is ApiResult.Success -> {
                    result.data?.let { userList = result.data }
                    Log.d("neotica", result.data?.get(0)?.username.toString())
                }
                is ApiResult.Error -> Log.d("neotica", result.errorMessage.toString())
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Blue),
        contentAlignment = Alignment.Center
    ) {
        auth = FirebaseAuth.getInstance()
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            item {
                LazyRow {
                    item {
                        userList.let {
                            for (items in it) {
                                Text(text = items.username.toString())
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
    }
}
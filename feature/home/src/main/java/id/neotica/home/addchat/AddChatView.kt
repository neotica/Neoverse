package id.neotica.home.addchat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import id.neotica.droidcore.component.alert.NeoToast
import id.neotica.droidcore.component.textfield.NeoTextField
import id.neotica.domain.ApiResult
import id.neotica.routes.Screen
import id.neotica.ui.shared.components.BasicScaffold
import id.neotica.ui.shared.components.LoadingIndicator
import id.neotica.ui.shared.components.NeoButton
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AddChatView(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: AddChatViewModel = koinViewModel(),
) {
    val username = remember { mutableStateOf("") }
    val state by viewModel.state.collectAsState()
    val toast = remember { mutableStateOf(false) }
    val toastMessage = remember { mutableStateOf("") }

    LaunchedEffect(state) {
        when (val result = state) {
            is ApiResult.Success -> {
                val conversation = result.data ?: return@LaunchedEffect
                viewModel.consumeState()
                navController.navigate(
                    Screen.Conversation(
                        conversationId = conversation.id,
                        peerUsername = conversation.peerUsername,
                    )
                ) {
                    popUpTo(Screen.AddChatScreen) { inclusive = true }
                }
            }
            is ApiResult.Error -> {
                toastMessage.value = result.errorMessage ?: "Unable to start chat"
                toast.value = true
                viewModel.consumeState()
            }
            else -> Unit
        }
    }

    BasicScaffold(
        topBarTitle = "New chat",
        navController = navController,
        paddingValues = paddingValues,
        enableActionBar = true,
    ) {
        Box(Modifier.fillMaxSize()) {
            Column(Modifier.padding(horizontal = 16.dp)) {
                Text("Enter the exact username you want to chat with.")
                Spacer(Modifier.padding(8.dp))
                NeoTextField(username, label = "Username")
                Spacer(Modifier.weight(1f))
                Row {
                    NeoButton(
                        text = "Start chat",
                        maxWidth = true,
                        enabled = username.value.isNotBlank() && state !is ApiResult.Loading,
                    ) {
                        viewModel.startChat(username.value)
                    }
                }
            }
            if (state is ApiResult.Loading) LoadingIndicator()
        }
    }
    if (toast.value) NeoToast(toastMessage.value, toast)
}

package id.neotica.neoverse.presentation.screen.friend

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.NavController
import id.neotica.droidcore.component.textfield.NeoTextField
import id.neotica.neoverse.presentation.navigation.Screen
import id.neotica.neoverse.presentation.ui.components.BasicScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun FriendView(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: FriendViewModel = koinViewModel()
) {
    BasicScreen(paddingValues = paddingValues) {
        val idValue = rememberSaveable { mutableStateOf("") }

        Text("Friend screen")
        NeoTextField(value = idValue)
        Button(onClick = {
            navController.navigate(Screen.SearchUserScreen)
//            viewModel.addFriend(idValue.value)
        }) {
            Text("Navigate to search")
        }
    }
}
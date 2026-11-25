package id.neotica.profile.presentation.deleteaccount

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import id.neotica.droidcore.component.alert.NeoToast
import id.neotica.droidcore.component.button.NeoButton
import id.neotica.droidcore.component.textfield.NeoTextField
import id.neotica.profile.presentation.ProfileViewModel
import id.neotica.routes.Screen
import id.neotica.ui.shared.components.BasicScaffold
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DeleteAccountView(
    navController: NavController,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val confirm = remember { mutableStateOf("") }
    val toast = remember { mutableStateOf(false) }
    val toastMessage = remember { mutableStateOf("") }
    val confirmText = "delete account"

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is ProfileViewModel.ProfileEvent.AccountDeleted -> {
                    navController.navigate(Screen.LoginScreen) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
                is ProfileViewModel.ProfileEvent.Error -> {
                    toastMessage.value = event.text
                    toast.value = true
                }
                is ProfileViewModel.ProfileEvent.Message -> {
                    toastMessage.value = event.text
                    toast.value = true
                }
            }
        }
    }

    BasicScaffold(
        topBarTitle = "Delete Account",
        true,
        navController = navController,
        paddingValues = paddingValues
    ) {
        Column(
            Modifier.padding(horizontal = 16.dp)
        ) {
            Text("To confirm account deletion, please type \"$confirmText\"")
            Spacer(Modifier.weight(1f))
            NeoTextField(confirm)
            Spacer(Modifier.padding(4.dp))
            Row {
                NeoButton(
                    text = "Confirm",
                    maxWidth = true,
                    enabled = confirm.value == confirmText,
                ) {
                    viewModel.deleteAccount()
                }
            }
        }
    }
    if (toast.value) NeoToast(toastMessage.value, toast)
}

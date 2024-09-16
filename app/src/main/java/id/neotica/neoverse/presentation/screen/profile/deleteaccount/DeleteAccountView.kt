package id.neotica.neoverse.presentation.screen.profile.deleteaccount

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import id.neotica.droidcore.component.textfield.NeoTextField
import id.neotica.neoverse.presentation.screen.profile.ProfileViewModel
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.NeoButton
import org.koin.androidx.compose.koinViewModel

@Composable
fun DeleteAccountView(
    navController: NavController,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = koinViewModel()
    ) {
    BasicScaffold(
        topBarTitle = "Delete Account",
        true,
        navController = navController,
        paddingValues = paddingValues
    ) {
        val confirm = remember { mutableStateOf("") }

        val confirmText = "delete account"

        Column(
            Modifier.padding(horizontal = 4.dp)
        ) {
            Text("To confirm account deletion, please type \"$confirmText\"")
            Spacer(Modifier.weight(1f))
            NeoTextField(confirm)
            Spacer(Modifier.padding(4.dp))
            Row(
//                Modifier.padding(horizontal = 22.dp)
            ) {
                NeoButton(
                    text = "Confirm",
                    true,
                    enabled = confirm.value == confirmText,
                ) {
                    viewModel.deleteAccount()
                }
            }
        }
    }
}
package id.neotica.profile.resetpassword

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import id.neotica.droidcore.component.textfield.NeoTextField
import id.neotica.profile.ProfileViewModel
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.NeoButton
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ResetPasswordView(
    navController: NavController,
    viewModel: ProfileViewModel = koinViewModel()
    ) {
    BasicScaffold(
        topBarTitle = "Reset Password",
        true,
        navController = navController,
    ) {

        val email = remember { mutableStateOf("") }
        val context = LocalContext.current

        Column(
            Modifier.padding(horizontal = 16.dp)
        ) {
            Text("Please type your email address.")
            Spacer(Modifier.weight(1f))
            NeoTextField(email)
            Spacer(Modifier.padding(4.dp))
            Row {
                NeoButton(
                    text = "Reset password",
                    maxWidth = true,
                    enabled = email.value.isNotEmpty(),
                ) {
                    viewModel.resetPassword(email = email.value)
                    Toast.makeText(context, "Please check your email inbox.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
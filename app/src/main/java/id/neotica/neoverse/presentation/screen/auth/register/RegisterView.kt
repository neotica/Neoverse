package id.neotica.neoverse.presentation.screen.auth.register

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import id.neotica.droidcore.component.textfield.NeoTextField
import id.neotica.droidcore.component.textfield.PasswordTextField
import id.neotica.neoverse.data.remote.ApiResult
import id.neotica.neoverse.presentation.navigation.RootScreen
import id.neotica.neoverse.presentation.ui.components.BasicScreen
import id.neotica.neoverse.presentation.ui.components.LoadingIndicatorBool
import org.koin.androidx.compose.koinViewModel

@Composable
fun RegisterView(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: RegisterViewModel = koinViewModel()
    ) {
    Column(modifier = Modifier.padding(paddingValues)) {
        val nameValue = remember { mutableStateOf("") }
        val emailValue = remember { mutableStateOf("") }
        val passwordValue = remember { mutableStateOf("") }
        var isButtonPressed by remember { mutableStateOf(false) }
        var isLoading by remember { mutableStateOf(false) }


        val context = LocalContext.current

        LaunchedEffect(isButtonPressed) {
            if (isButtonPressed) {
                viewModel.registerStatus.collect { result ->
                    when (result) {
                        is ApiResult.Loading -> isLoading = true
                        is ApiResult.Success -> {
                        isLoading = false
                            navController.navigate(RootScreen.HomeNav)
                            Toast.makeText(context, "Success registered ${nameValue.value}", Toast.LENGTH_SHORT).show()
                        }

                        is ApiResult.Error -> {
                        isLoading = false
                            Toast.makeText(context, result.errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        BasicScreen(paddingValues = paddingValues) {
            LoadingIndicatorBool(isLoading = isLoading) {
                Text("Register")
                NeoTextField(value = nameValue, label = "Name")
                NeoTextField(value = emailValue, label = "Email")
                PasswordTextField(value = passwordValue, placeholder = "Password")
                Button(
                    enabled = nameValue.value.isNotEmpty() && emailValue.value.isNotEmpty() && passwordValue.value.isNotEmpty(),
                    onClick = {
                    isButtonPressed = true
                    viewModel.register(nameValue.value, emailValue.value, passwordValue.value)
                }) {
                    Text(text = "Register")
                }
            }
        }
    }
}
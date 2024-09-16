package id.neotica.neoverse.presentation.screen.auth.register

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import id.neotica.droidcore.component.textfield.NeoTextField
import id.neotica.droidcore.component.textfield.PasswordTextField
import id.neotica.domain.ApiResult
import id.neotica.neoverse.presentation.navigation.Screen
import id.neotica.neoverse.presentation.ui.components.BasicScaffold
import id.neotica.neoverse.presentation.ui.components.LoadingIndicatorBool
import id.neotica.neoverse.presentation.ui.components.NeoButton
import org.koin.androidx.compose.koinViewModel

@Composable
fun RegisterView(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: RegisterViewModel = koinViewModel()
    ) {
    val nameValue = remember { mutableStateOf("") }
    val emailValue = remember { mutableStateOf("") }
    val passwordValue = remember { mutableStateOf("") }
    var isButtonPressed by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var isUserExist by remember { mutableStateOf(false) }
    var onValueChange by remember { mutableStateOf(false) }


    val context = LocalContext.current

    LaunchedEffect(isButtonPressed) {
        if (isButtonPressed) {
            viewModel.registerStatus.collect { result ->
                when (result) {
                    is ApiResult.Loading -> isLoading = true
                    is ApiResult.Success -> {
                        isLoading = false
                        navController.navigate(Screen.VerifyEmailScreen)
                        Toast.makeText(
                            context,
                            "Success registered ${nameValue.value}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is ApiResult.Error -> {
                        isLoading = false
                        Toast.makeText(context, result.errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.isUserExist.collect { result ->
            when(result) {
                is ApiResult.Loading -> isUserExist = false
                is ApiResult.Success -> {
                    val user = result.data
                    user?.let {
                        isUserExist = !it
                    }
                }
                is ApiResult.Error -> isUserExist = false
            }
        }
    }

    LaunchedEffect(onValueChange) {
        viewModel.checkUsername(nameValue.value)
    }

    BasicScaffold(
        "Register",
        true,
        navController = navController
    ) {
        Box(Modifier.padding(4.dp)) {
            Column {
                LoadingIndicatorBool(isLoading = isLoading) {
                    Box(Modifier.weight(1f)) {
                        Column {
                            NeoTextField(
                                value = nameValue, label = "Username",
                                onValueChange = {
                                    onValueChange = !onValueChange
                                }
                            )
                            NeoTextField(value = emailValue, label = "Email")
                            Spacer(Modifier.padding(4.dp))
                            PasswordTextField(value = passwordValue, placeholder = "Password")
                        }
                    }
                    Row(
                        Modifier.padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Column {
                            when {
                                nameValue.value.isEmpty() -> Text("Please fill all the fields.")
                                !isUserExist -> Text("Username is already used.")
                            }
                            NeoButton(
                                "Register",
                                maxWidth = true,
                                enabled = nameValue.value.isNotEmpty()
                                        && emailValue.value.isNotEmpty()
                                        && passwordValue.value.isNotEmpty()
                                        && isUserExist,
                            ) {
                                isButtonPressed = true
                                viewModel.register(nameValue.value, emailValue.value, passwordValue.value)
                            }
                        }
                    }
                    Spacer(Modifier.padding(8.dp))
                }
            }
        }
    }
}
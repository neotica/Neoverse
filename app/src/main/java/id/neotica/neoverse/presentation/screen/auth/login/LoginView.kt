package id.neotica.neoverse.presentation.screen.auth.login

import android.util.Log
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import id.neotica.droidcore.component.textfield.NeoTextField
import id.neotica.droidcore.component.textfield.PasswordTextField
import id.neotica.neoverse.data.remote.ApiResult
import id.neotica.neoverse.presentation.navigation.RootScreen
import id.neotica.neoverse.presentation.navigation.Screen
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginView(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: LoginViewModel = koinViewModel()) {

    var isButtonPressed by remember { mutableStateOf(false) }
    val emailValue = rememberSaveable { mutableStateOf("") }
    val passwordValue = rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current

    LaunchedEffect(isButtonPressed) {
        if (isButtonPressed) {
            viewModel.loginCred.collect { result ->
                when(result) {
                    is ApiResult.Loading -> {
                        Log.d("neotica", "loading")
                        Toast.makeText(context, "Loading...", Toast.LENGTH_SHORT).show()
                    }
                    is ApiResult.Success -> {
                        Log.d("neotica", "success ${result.data}")
                        Toast.makeText(context, result.data, Toast.LENGTH_SHORT).show()
                        navController.navigate(RootScreen.HomeNav)
                    }
                    is ApiResult.Error -> {
                        Log.d("neotica", "error: ${result.errorMessage} data: ${result.data}")
                        Toast.makeText(context, result.errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    Column(modifier = Modifier.padding(paddingValues)) {

        val context = LocalContext.current
        Text("Login")
        NeoTextField(value = emailValue, label = "Email")
        PasswordTextField(value = passwordValue)
        Button(onClick = {
            isButtonPressed = true
            viewModel.login(emailValue.value, passwordValue.value)
        }) {
            Text("Login")
        }
        Button(onClick = {
            navController.navigate(Screen.RegisterScreen)
        }) {
            Text("Register")
        }
    }
}
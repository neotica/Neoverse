package id.neotica.neoversewear.presentation.screen.auth

import android.util.Log
import android.widget.Toast
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
import androidx.wear.compose.material.Text
import id.neotica.domain.ApiResult
import id.neotica.droidcore.component.textfield.NeoTextField
import id.neotica.droidcore.component.textfield.PasswordTextField
import id.neotica.neoversewear.presentation.components.BasicWearScreen
import id.neotica.neoversewear.presentation.navigation.SpacerItem
import id.neotica.neoversewear.presentation.navigation.WearScreen
import id.neotica.ui.components.NeoButton
import org.koin.androidx.compose.koinViewModel

@Composable
fun WearLoginView(navController: NavController, modifier: Modifier = Modifier, viewModel: WLoginViewModel = koinViewModel()) {
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
                        navController.navigate(WearScreen.HomeNavScreen.toString()) {
                            popUpTo(WearScreen.HomeNavScreen.toString()) {
                                inclusive = true
                            }
                            launchSingleTop
                        }
                    }
                    is ApiResult.Error -> {
                        Log.d("neotica", "error: ${result.errorMessage} data: ${result.data}")
                        Toast.makeText(context, result.errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    BasicWearScreen {
        item { SpacerItem() }
        item { Text("Please Login to your Neoverse Account.") }
        item { SpacerItem() }
        item { NeoTextField(value = emailValue, label = "Email") }
        item { SpacerItem() }
        item { PasswordTextField(value = passwordValue, placeholder = "Password") }
        item { SpacerItem() }
        item {
            NeoButton(
                text = "Login",
                enabled = emailValue.value.isNotEmpty() && passwordValue.value.isNotEmpty(),
                maxWidth = true
            ) {
                isButtonPressed = true
                viewModel.login(emailValue.value, passwordValue.value)
            }
        }
        item { SpacerItem() }
    }
}
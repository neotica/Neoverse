package id.neotica.auth.login

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import id.neotica.droidcore.component.textfield.NeoTextField
import id.neotica.droidcore.component.textfield.PasswordTextField
import id.neotica.domain.ApiResult
import id.neotica.ui.navigation.RootScreen
import id.neotica.ui.navigation.Screen
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.NeoButton
import id.neotica.ui.theme.DarkPrimary
import org.koin.androidx.compose.koinViewModel
import androidx.compose.animation.core.rememberInfiniteTransition as rememberInfiniteTransition1

@Composable
fun LoginView(
    navController: NavController,
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
                        navController.navigate(RootScreen.HomeNav) {
                            popUpTo(RootScreen.HomeNav) {
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

    val infiniteTransition = rememberInfiniteTransition1()

    // Animate a float value from 0f to 360f to represent rotation
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    BasicScaffold("Login") {
        Image(
            painterResource(
            id.neotica.ui.R.drawable.logo_app),
            contentDescription = "Neoverse Logo",
            modifier = Modifier.fillMaxWidth().size(200.dp)
                .graphicsLayer(rotationZ = rotationAngle)
        )
        Box(Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
            Column {
                Box(
                    modifier = Modifier.weight(1f),
                ) {
                    Column {
                        NeoTextField(value = emailValue, label = "Email")
                        Spacer(modifier = Modifier.padding(4.dp))
                        PasswordTextField(value = passwordValue, placeholder = "Password")
                    }
                }
                Column(
                    Modifier.padding(horizontal = 20.dp)
                ) {
                    NeoButton(
                        text = "Login",
                        enabled = emailValue.value.isNotEmpty() && passwordValue.value.isNotEmpty(),
                        maxWidth = true
                    ) {
                        isButtonPressed = true
                        viewModel.login(emailValue.value, passwordValue.value)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Haven't made an account? ",
                            fontSize = 14.sp,
                            )
                        Text(
                            color = DarkPrimary,
                            fontSize = 14.sp,
                            text = "Register.",
                            modifier = Modifier.clickable {
                                navController.navigate(Screen.RegisterScreen)
                            }
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Forgot Password? ",
                            fontSize = 14.sp,
                        )
                        Text(
                            color = DarkPrimary,
                            fontSize = 14.sp,
                            text = "Reset Password.",
                            modifier = Modifier.clickable {
                                navController.navigate(Screen.ResetPassword)
                            }
                        )
                    }
                    Spacer(Modifier.padding(8.dp))
                }
            }
        }
    }
}
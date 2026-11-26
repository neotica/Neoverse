package id.neotica.auth.presentation.login

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.painterResource
import id.neotica.res.MR
import id.neotica.state.AppVariant
import id.neotica.ui.shared.components.BasicScaffold
import id.neotica.ui.shared.components.LoadingIndicator
import id.neotica.ui.shared.components.NeoButton
import id.neotica.ui.shared.components.ToastHost
import id.neotica.ui.shared.components.textfield.ControlledNeoTextField
import id.neotica.ui.shared.components.textfield.ControlledPasswordTextField
import id.neotica.ui.shared.theme.DarkPrimary
import id.neotica.ui.shared.theme.FontSize
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun LoginView(
    state: LoginState,
    appVariant: AppVariant,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onSubmit: () -> Unit,
    onRegister: () -> Unit,
    onForgotPassword: () -> Unit,
    onToastDismissed: () -> Unit,
) {
    val infiniteTransition = rememberInfiniteTransition()
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
    )

    BasicScaffold("Login", topBarVisibility = false) {
        Image(
            painterResource(appVariant.logo),
            contentDescription = "${appVariant.appName} Logo",
            modifier = when (appVariant.appName) {
                "Neoverse" -> Modifier.fillMaxWidth().size(200.dp).graphicsLayer(rotationZ = rotationAngle)
                "Alexandria" -> Modifier.fillMaxWidth().size(180.dp)
                else -> Modifier
            },
        )
        Box(Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
            Column {
                Box(Modifier.weight(1f)) {
                    Column {
                        ControlledNeoTextField(
                            value = state.username,
                            onValueChange = onUsernameChange,
                            label = "Username",
                        )
                        Spacer(Modifier.padding(4.dp))
                        ControlledPasswordTextField(
                            value = state.password,
                            onValueChange = onPasswordChange,
                            isPasswordVisible = state.isPasswordVisible,
                            onTogglePasswordVisibility = onTogglePasswordVisibility,
                            placeholder = "Password",
                        )
                    }
                }
                Column(Modifier.padding(horizontal = 20.dp)) {
                    NeoButton(
                        text = "Login",
                        enabled = state.canSubmit,
                        maxWidth = true,
                        onClick = onSubmit,
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = "Haven't made an account? ",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = FontSize.Smaller,
                        )
                        Text(
                            color = DarkPrimary,
                            fontSize = FontSize.Smaller,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            text = "Register.",
                            modifier = Modifier.clickable(onClick = onRegister),
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = "Forgot Password? ",
                            overflow = TextOverflow.Ellipsis,
                            fontSize = FontSize.Smaller,
                        )
                        Text(
                            color = DarkPrimary,
                            fontSize = FontSize.Smaller,
                            text = "Reset Password.",
                            modifier = Modifier.clickable(onClick = onForgotPassword),
                        )
                    }
                    Spacer(Modifier.padding(8.dp))
                }
            }
            if (state.isLoading) {
                Box(Modifier.fillMaxSize().clickable(enabled = true) {}) { LoadingIndicator() }
            }
        }
    }
    ToastHost(state.toastMessage, onToastDismissed)
}

@Preview
@Composable
private fun LoginViewPreview() {
    LoginView(
        state = LoginState(username = "jdoe", password = "secret"),
        appVariant = AppVariant("Neoverse", MR.images.logo_neoverse),
        onUsernameChange = {},
        onPasswordChange = {},
        onTogglePasswordVisibility = {},
        onSubmit = {},
        onRegister = {},
        onForgotPassword = {},
        onToastDismissed = {},
    )
}

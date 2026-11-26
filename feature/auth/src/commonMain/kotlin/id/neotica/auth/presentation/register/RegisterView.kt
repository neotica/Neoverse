package id.neotica.auth.presentation.register

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.neotica.ui.shared.components.BasicScaffold
import id.neotica.ui.shared.components.LoadingIndicator
import id.neotica.ui.shared.components.NeoButton
import id.neotica.ui.shared.components.ToastHost
import id.neotica.ui.shared.components.textfield.ControlledNeoTextField
import id.neotica.ui.shared.components.textfield.ControlledPasswordTextField
import id.neotica.ui.shared.theme.DarkPrimary
import id.neotica.ui.shared.theme.FontSize
import id.neotica.ui.shared.theme.NegativePrimary
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun RegisterView(
    state: RegisterState,
    onUsernameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onAgreedChange: (Boolean) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit,
    onPrivacyPolicy: () -> Unit,
    onTermsOfService: () -> Unit,
    onToastDismissed: () -> Unit,
) {
    BasicScaffold(
        topBarTitle = "Register",
        enableActionBar = true,
        customActionBar = onBack,
    ) {
        Box(Modifier.padding(vertical = 4.dp, horizontal = 16.dp)) {
            Column {
                Box(Modifier.weight(1f)) {
                    Column {
                        ControlledNeoTextField(
                            value = state.username,
                            onValueChange = onUsernameChange,
                            label = "Username",
                            isError = state.usernameError != null,
                        )
                        state.usernameError?.let {
                            Text(it, color = NegativePrimary, fontSize = FontSize.Small)
                        }
                        ControlledNeoTextField(
                            value = state.email,
                            onValueChange = onEmailChange,
                            label = "Email",
                        )
                        Spacer(Modifier.padding(4.dp))
                        ControlledPasswordTextField(
                            value = state.password,
                            onValueChange = onPasswordChange,
                            isPasswordVisible = state.isPasswordVisible,
                            onTogglePasswordVisibility = onTogglePasswordVisibility,
                            placeholder = "Password",
                        )
                        Spacer(Modifier.padding(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            Text(
                                color = DarkPrimary,
                                fontSize = 14.sp,
                                text = "Privacy Policy",
                                modifier = Modifier.clickable(onClick = onPrivacyPolicy),
                            )
                            Spacer(Modifier.weight(1f))
                            Text(
                                color = DarkPrimary,
                                fontSize = 14.sp,
                                text = "Terms of Service",
                                modifier = Modifier.clickable(onClick = onTermsOfService),
                            )
                        }
                        Spacer(Modifier.padding(16.dp))
                        Text("By registering, you are already accepting the Terms and Service and Privacy Policy of this app.")
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Yes, I agree.")
                            Checkbox(state.agreedToTerms, onCheckedChange = onAgreedChange)
                        }
                    }
                }
                Row(
                    Modifier.padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    NeoButton(
                        text = "Register",
                        maxWidth = true,
                        enabled = state.canSubmit,
                        onClick = onSubmit,
                    )
                }
            }
            if (state.isLoading) LoadingIndicator()
        }
    }
    ToastHost(state.toastMessage, onToastDismissed)
}

@Preview
@Composable
private fun RegisterViewPreview() {
    RegisterView(
        state = RegisterState(username = "jdoe", email = "jdoe@example.com", password = "secret", agreedToTerms = true),
        onUsernameChange = {},
        onEmailChange = {},
        onPasswordChange = {},
        onAgreedChange = {},
        onTogglePasswordVisibility = {},
        onSubmit = {},
        onBack = {},
        onPrivacyPolicy = {},
        onTermsOfService = {},
        onToastDismissed = {},
    )
}

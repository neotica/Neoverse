package id.neotica.profile.presentation.resetpassword

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import id.neotica.ui.shared.components.BasicScaffold
import id.neotica.ui.shared.components.LoadingIndicator
import id.neotica.ui.shared.components.NeoButton
import id.neotica.ui.shared.components.ToastHost
import id.neotica.ui.shared.components.textfield.ControlledNeoTextField
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ResetPasswordView(
    state: ResetPasswordState,
    onEmailChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit,
    onToastDismissed: () -> Unit,
) {
    BasicScaffold(
        topBarTitle = "Reset Password",
        enableActionBar = true,
        customActionBar = onBack,
    ) {
        Column(Modifier.padding(horizontal = 16.dp)) {
            Text("Please type your email address.")
            Spacer(Modifier.weight(1f))
            ControlledNeoTextField(
                value = state.email,
                onValueChange = onEmailChange,
                label = "Email",
            )
            Spacer(Modifier.padding(4.dp))
            Row {
                NeoButton(
                    text = "Reset password",
                    maxWidth = true,
                    enabled = state.canSubmit,
                    onClick = onSubmit,
                )
            }
            if (state.isLoading) LoadingIndicator()
        }
    }
    ToastHost(state.toastMessage, onToastDismissed)
}

@Preview
@Composable
private fun ResetPasswordViewPreview() {
    ResetPasswordView(
        state = ResetPasswordState(email = "jdoe@example.com"),
        onEmailChange = {},
        onSubmit = {},
        onBack = {},
        onToastDismissed = {},
    )
}

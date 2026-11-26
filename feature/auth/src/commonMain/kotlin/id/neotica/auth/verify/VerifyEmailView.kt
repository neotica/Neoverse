package id.neotica.auth.verify

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.neotica.ui.shared.components.BasicScaffold
import id.neotica.ui.shared.components.NeoButton
import id.neotica.ui.shared.components.ToastHost
import id.neotica.ui.shared.theme.DarkPrimary
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun VerifyEmailView(
    state: VerifyEmailState,
    paddingValues: PaddingValues,
    onResend: () -> Unit,
    onVerified: () -> Unit,
    onToastDismissed: () -> Unit,
) {
    BasicScaffold(
        "Verify email",
        horizontalAlignment = Alignment.CenterHorizontally,
        paddingValues = paddingValues,
    ) {
        Box(Modifier.padding(4.dp)) {
            Column {
                Box(Modifier.weight(1f)) {
                    Text(
                        text = "Your account has been created. Verify your email address " +
                            "using the link sent to ${state.email.ifBlank { "your email" }}.",
                    )
                }
                Row(
                    Modifier.padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    NeoButton(
                        text = "My email is verified",
                        maxWidth = true,
                        onClick = onVerified,
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text(text = "Didn't receive an email? ", fontSize = 14.sp)
                    Text(
                        color = DarkPrimary,
                        fontSize = 14.sp,
                        text = "Resend email.",
                        modifier = Modifier.clickable {
                            if (state.canResend) onResend()
                        },
                    )
                }
                Spacer(Modifier.padding(8.dp))
            }
        }
    }
    ToastHost(state.toastMessage, onToastDismissed)
}

@Preview
@Composable
private fun VerifyEmailViewPreview() {
    VerifyEmailView(
        state = VerifyEmailState(email = "jdoe@example.com"),
        paddingValues = PaddingValues(),
        onResend = {},
        onVerified = {},
        onToastDismissed = {},
    )
}

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import id.neotica.droidcore.component.alert.NeoToast
import id.neotica.routes.Screen
import id.neotica.ui.shared.components.BasicScaffold
import id.neotica.ui.shared.components.NeoButton
import id.neotica.ui.shared.theme.DarkPrimary
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun VerifyEmailView(
    navController: NavController,
    paddingValues: PaddingValues,
    email: String,
    modifier: Modifier = Modifier,
    viewModel: VerifyEmailViewModel = koinViewModel()
) {
    var toast = remember { mutableStateOf(false) }
    var toastMessage = remember { mutableStateOf("") }

    fun backToLogin() {
        viewModel.logout()
        navController.navigate(Screen.LoginScreen) {
            popUpTo(0) { inclusive = true }
            launchSingleTop = true
        }
    }

    BasicScaffold(
        "Verify email",
        horizontalAlignment = Alignment.CenterHorizontally,
        paddingValues = paddingValues
    ) {
        Box(Modifier.padding(4.dp)) {
            Column {
                Box(Modifier.weight(1f)) {
                    Column {
                        Text(
                            text = "Your account has been created. Verify your email address " +
                                    "using the link sent to ${email.ifBlank { "your email" }}.",
                        )
                    }
                }
                Row(
                    Modifier.padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    NeoButton(
                        "My email is verified",
                        maxWidth = true
                    ) {
                        backToLogin()
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Didn't receive an email? ",
                        fontSize = 14.sp,
                    )
                    Text(
                        color = DarkPrimary,
                        fontSize = 14.sp,
                        text = "Resend email.",
                        modifier = Modifier.clickable {
                            if (email.isBlank()) {
                                toastMessage.value = "Email address is unavailable."
                                toast.value = true
                            } else {
                                viewModel.resend(email)
                                toastMessage.value = "Email sent."
                                toast.value = true
                            }
                        }
                    )
                }
                Spacer(Modifier.padding(8.dp))
            }
        }
    }
    if (toast.value) NeoToast(toastMessage.value, toast)
}

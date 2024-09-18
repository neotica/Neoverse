package id.neotica.neoverse.presentation.screen.auth.verify

import android.widget.Toast
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import id.neotica.ui.navigation.RootScreen
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.NeoButton
import id.neotica.ui.theme.DarkPrimary
import org.koin.androidx.compose.koinViewModel

@Composable
fun VerifyEmailView(
    navController: NavController,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier,
    viewModel: VerifyEmailViewModel = koinViewModel()
) {
    LaunchedEffect(Unit) {
        if (viewModel.isUserVerified()) {
            viewModel.logout()
            navController.navigate(RootScreen.AuthNav)
        } else
            viewModel.sendEmailVerification()
    }
    BasicScaffold(
        "Verify email",
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(paddingValues)
    ) {
        Box(Modifier.padding(4.dp)) {
            Column {
                Box(Modifier.weight(1f)) {
                    Column {
                        Text(
                            text = "You have been successfully registered. " +
                                    "Now kindly verify your email address " +
                                    "with the link that we had just sent to your email address.",
                        )
                    }
                }
                Row(
                    Modifier.padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    NeoButton(
                        "My Email is verified",
                        true
                    ) {
                        viewModel.logout()
                        navController.navigate(RootScreen.AuthNav)
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
                    val context = LocalContext.current
                    Text(
                        color = DarkPrimary,
                        fontSize = 14.sp,
                        text = "Resend email.",
                        modifier = Modifier.clickable {
                            viewModel.sendEmailVerification()
                            Toast.makeText(context, "Email sent.", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
                Spacer(Modifier.padding(8.dp))
            }
        }
    }
}
package id.neotica.profile.presentation.accountsettings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import id.neotica.ui.shared.components.BasicScaffold
import id.neotica.ui.shared.components.MenuItem
import id.neotica.ui.shared.theme.NegativePrimary
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AccountSettingView(
    state: AccountSettingState,
    paddingValues: PaddingValues,
    onBack: () -> Unit,
    onResetPassword: () -> Unit,
    onDeleteAccount: () -> Unit,
) {
    BasicScaffold(
        topBarTitle = "Account Settings",
        horizontalAlignment = Alignment.CenterHorizontally,
        paddingValues = paddingValues,
        enableActionBar = true,
        customActionBar = onBack,
    ) {
        Column(Modifier.padding(16.dp)) {
            MenuItem("Reset Password", onClick = onResetPassword)
            MenuItem(
                title = "Delete Account",
                textColor = NegativePrimary,
                onClick = onDeleteAccount,
            )
        }
    }
}

@Preview
@Composable
private fun AccountSettingViewPreview() {
    AccountSettingView(
        state = AccountSettingState(),
        paddingValues = PaddingValues(),
        onBack = {},
        onResetPassword = {},
        onDeleteAccount = {},
    )
}

package id.neotica.profile.presentation.deleteaccount

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
fun DeleteAccountView(
    state: DeleteAccountState,
    paddingValues: PaddingValues,
    onConfirmationChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onBack: () -> Unit,
    onToastDismissed: () -> Unit,
) {
    BasicScaffold(
        topBarTitle = "Delete Account",
        enableActionBar = true,
        customActionBar = onBack,
        paddingValues = paddingValues,
    ) {
        Column(Modifier.padding(horizontal = 16.dp)) {
            Text("To confirm account deletion, please type \"delete account\"")
            Spacer(Modifier.weight(1f))
            ControlledNeoTextField(
                value = state.confirmation,
                onValueChange = onConfirmationChange,
                label = "Confirmation",
            )
            Spacer(Modifier.padding(4.dp))
            Row {
                NeoButton(
                    text = "Confirm",
                    maxWidth = true,
                    enabled = state.canConfirm,
                    onClick = onConfirm,
                )
            }
            if (state.isLoading) LoadingIndicator()
        }
    }
    ToastHost(state.toastMessage, onToastDismissed)
}

@Preview
@Composable
private fun DeleteAccountViewPreview() {
    DeleteAccountView(
        state = DeleteAccountState(confirmation = "delete account"),
        paddingValues = PaddingValues(),
        onConfirmationChange = {},
        onConfirm = {},
        onBack = {},
        onToastDismissed = {},
    )
}

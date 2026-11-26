package id.neotica.home.addchat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
fun AddChatView(
    state: AddChatState,
    paddingValues: PaddingValues,
    onUsernameChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit,
    onToastDismissed: () -> Unit,
) {
    BasicScaffold(
        topBarTitle = "New chat",
        paddingValues = paddingValues,
        enableActionBar = true,
        customActionBar = onBack,
    ) {
        Box(Modifier.fillMaxSize()) {
            Column(Modifier.padding(horizontal = 16.dp)) {
                Text("Enter the exact username you want to chat with.")
                Spacer(Modifier.padding(8.dp))
                ControlledNeoTextField(
                    value = state.username,
                    onValueChange = onUsernameChange,
                    label = "Username",
                )
                Spacer(Modifier.weight(1f))
                Row {
                    NeoButton(
                        text = "Start chat",
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
private fun AddChatViewPreview() {
    AddChatView(
        state = AddChatState(username = "jdoe"),
        paddingValues = PaddingValues(),
        onUsernameChange = {},
        onSubmit = {},
        onBack = {},
        onToastDismissed = {},
    )
}

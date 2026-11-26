package id.neotica.ui.shared.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import id.neotica.droidcore.component.alert.NeoToast

@Composable
fun ToastHost(
    message: String?,
    onDismiss: () -> Unit,
) {
    if (message == null) return
    val appear = remember(message) { mutableStateOf(true) }
    NeoToast(message, appear)
    LaunchedEffect(appear.value) {
        if (!appear.value) onDismiss()
    }
}

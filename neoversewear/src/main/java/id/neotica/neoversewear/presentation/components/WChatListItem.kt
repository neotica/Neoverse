package id.neotica.neoversewear.presentation.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.CardDefaults
import id.neotica.droidcore.component.alert.NeoAlert
import id.neotica.ui.components.NeoButton
import id.neotica.ui.theme.DarkBackground
import id.neotica.ui.theme.DarkPrimary

@Composable
fun WChatListItem(
    name: String,
    image: String,
    paddingValues: PaddingValues = PaddingValues(),
    content: @Composable () -> Unit = {},
    alertTitle: String = "",
    alertButton: String = "",
    enableLongClick: Boolean = false,
    onLongClick: () -> Unit = {},
    onClick: () -> Unit = {}
) {
    val openDialog = remember { mutableStateOf(false) }

    if (openDialog.value) {
        NeoAlert(
            openDialog = openDialog,
            title = alertTitle,
            backButton = "Cancel",
            confirmButton = {
                NeoButton(
                    alertButton,
                    negative = true
                ) {
                    onLongClick()
                    openDialog.value = false
                }
            },
        )
    }

    Card(
        onClick = { onClick() },
        backgroundPainter = CardDefaults.cardBackgroundPainter(
            startBackgroundColor = DarkPrimary,
            endBackgroundColor = DarkBackground
        ),
        contentColor = Color.White
    ) {
        content()
    }
}
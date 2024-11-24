package id.neotica.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import id.neotica.droidcore.component.alert.NeoAlert
import id.neotica.ui.theme.DarkPrimaryTransparent
import id.neotica.ui.theme.Transparent

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatListItem(
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

    val cardColorsPrime = CardColors(
        containerColor = DarkPrimaryTransparent,
        contentColor = Color.White,
        disabledContentColor = DarkPrimaryTransparent,
        disabledContainerColor = DarkPrimaryTransparent
    )

    val cardColorsTransparent = CardColors(
        containerColor = Transparent,
        contentColor = Color.White,
        disabledContentColor = Transparent,
        disabledContainerColor = Transparent
    )

    val longClick: (() -> Unit)? = if (enableLongClick) {
        { openDialog.value = true }
    } else {
        null
    }

    val haptic = LocalHapticFeedback.current

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
        colors = cardColorsTransparent,
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { onClick() },
                onLongClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    longClick?.invoke()
                },
            )
            .padding(paddingValues),
    ) {
        Row {
            Box(
                Modifier.padding(2.dp)
            ) {
                ImageCoil(
                    url = image,
                    fillMaxWidth = false,
                    size = 72.dp,
                    contentAlignment = Alignment.CenterStart
                )
            }
            Column(
                Modifier.padding(8.dp),
            ) {
                content()
            }
        }
    }
}
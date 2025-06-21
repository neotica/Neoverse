package id.neotica.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Unspecified
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import id.neotica.droidcore.component.alert.NeoToast

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DropdownItem(
    title: String,
    textColor: Color = Unspecified,
    custom: (@Composable () -> Unit)? = null,
    onClick: () -> Unit = {},
) {
    val haptic = LocalHapticFeedback.current
    val toast = remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
//            .fillMaxWidth()
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = White),
                onClick = onClick,
                onLongClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    toast.value = true
                },
            )
            .padding(vertical = 4.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (custom != null) custom() else {
            Text(
                text = title,
                color = textColor
            )
        }
    }
    if (toast.value) NeoToast(title, toast)
}
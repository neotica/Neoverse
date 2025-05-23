package id.neotica.ui.shared.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Unspecified
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import neoverse.core.ui_shared.generated.resources.Res
import neoverse.core.ui_shared.generated.resources.ic_arrow_forward
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MenuItem(
    title: String,
    textColor: Color = Unspecified,
    leadingIcon: DrawableResource? = null,
    iconTint: Color = LocalContentColor.current,
    onClick: () -> Unit = {},
) {
    val haptic = LocalHapticFeedback.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = White),
                onClick = onClick,
                onLongClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
//                    Toast.makeText(context, title, Toast.LENGTH_SHORT).show()
                },
            )
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (leadingIcon != null) {
            Icon(
                painter = painterResource(leadingIcon),
                contentDescription = "Change Profile Picture",
                modifier = Modifier.padding(end = 8.dp),
                tint = iconTint
            )
        }
        Text(
            text = title,
            color = textColor,
            modifier = Modifier.weight(1f)
        )
        Icon(
            modifier = Modifier,
            painter = painterResource(Res.drawable.ic_arrow_forward),
            contentDescription = "",
            tint = White
        )
    }
}
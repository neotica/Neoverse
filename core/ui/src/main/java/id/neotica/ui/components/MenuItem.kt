package id.neotica.ui.components

import android.widget.Toast
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import id.neotica.ui.R

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MenuItem(
    title: String,
    textColor: Color = Unspecified,
    leadingIcon: Int? = null,
    iconTint: Color = LocalContentColor.current,
    onClick: () -> Unit = {},
) {
    val context = LocalContext.current
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
                    Toast.makeText(context, title, Toast.LENGTH_SHORT).show()
                },
            )
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = ImageVector.vectorResource(leadingIcon),
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
            imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_forward),
            contentDescription = "",
            tint = White
        )
    }
}
package id.neotica.neoverse.presentation.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import id.neotica.neoverse.presentation.ui.theme.DarkPrimary

@Composable
fun NeoButton(
    text: String,
    maxWidth: Boolean = false,
    width: Dp = Dp.Unspecified,
    enabled: Boolean = true,
    onClick: () -> Unit = {}
) {
    val defaultModifier = Modifier.width(width)
    val maxWidthModifier = Modifier.fillMaxWidth()
    Button(
        modifier = if (maxWidth) maxWidthModifier else defaultModifier
        ,
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = DarkPrimary,
            contentColor = Color.White,
        ),
        shape = RoundedCornerShape(20)
    ) {
        Text(text)
    }
}
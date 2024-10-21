package id.neotica.neoversewear.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Text
import id.neotica.ui.theme.DarkPrimary
import id.neotica.ui.theme.NegativePrimary

@Composable
fun NeoWButton(
    label: String,
    negative: Boolean = false,
    enabled: Boolean = true,
    fillMaxWidth: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val buttonColors = if (negative) {
        ButtonDefaults.buttonColors(
            backgroundColor = NegativePrimary,
            contentColor = Color.White
        )
    } else {
        ButtonDefaults.buttonColors(
            backgroundColor = DarkPrimary,
            contentColor = Color.White
        )
    }

    Button(
        onClick = onClick,
        colors = buttonColors,
        enabled = enabled
    ) { Text(label) }
}
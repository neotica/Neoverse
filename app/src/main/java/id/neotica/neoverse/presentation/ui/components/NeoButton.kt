package id.neotica.neoverse.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import id.neotica.neoverse.presentation.ui.theme.DarkPrimary

@Composable
fun NeoButton(
    text: String,
    width: Dp = Dp.Unspecified,
    onClick: () -> Unit = {}
) {
    Button(
        modifier = Modifier
            .width(width)
        ,
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = DarkPrimary,
            contentColor = Color.White,
        ),
        shape = RoundedCornerShape(20)
    ) {
        Text(text)
    }
}
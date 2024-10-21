package id.neotica.neoversewear.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Text
import id.neotica.ui.theme.DarkPrimary

@Composable
fun NeoWChip(
    label: String,
    fillMaxWidth: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Chip(
        label = { Text(label) },
        onClick = onClick,
        colors = ChipDefaults.chipColors(
            backgroundColor = DarkPrimary
        ),
        modifier = if (fillMaxWidth) Modifier.fillMaxWidth() else modifier
    )
}
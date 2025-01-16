package id.neotica.neoversewear.presentation.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun SpacerItem(padding: Dp = 4.dp) {
    Spacer(Modifier.padding(padding))
}
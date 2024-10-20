package id.neotica.neoversewear.presentation.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText

@Composable
fun WearHomeScreen(modifier: Modifier = Modifier) {
    TimeText()
    ScalingLazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center
    ) {
        item { Text("Hello") }
        item { Text("Hello") }
        item { Text("Hello") }
        item { Text("Hello") }
        item { Text("Hello") }
    }
}
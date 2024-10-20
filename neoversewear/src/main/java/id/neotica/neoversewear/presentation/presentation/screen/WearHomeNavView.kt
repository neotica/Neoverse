package id.neotica.neoversewear.presentation.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import id.neotica.neoversewear.presentation.presentation.navigation.WearScreen

@Composable
fun WearHomeNavView(navController: NavController, modifier: Modifier = Modifier) {
    TimeText()
    ScalingLazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center
    ) {
        item {
            Chip(label = { Text("Home") }, onClick = {
                navController.navigate(WearScreen.HomeScreen.toString())
            })
        }
        item { Spacer(Modifier.padding(4.dp)) }
        item {
            Chip(label = { Text("Profile") }, onClick = {})
        }
    }
}
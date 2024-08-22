package id.neotica.neoverse.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SettingView(paddingValues: PaddingValues) {
    Column(modifier = Modifier.padding(paddingValues)) {
        Text("hello")
    }
}
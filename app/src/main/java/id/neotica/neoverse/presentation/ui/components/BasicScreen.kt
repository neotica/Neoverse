package id.neotica.neoverse.presentation.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BasicScreen(
    paddingValues: PaddingValues,
    isLoading: Boolean = false,
    horizontalAlign: Alignment.Horizontal = Alignment.Start,
    content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxSize(),
                horizontalAlignment = horizontalAlign
            ) {
                item { content() }
            }
        }
    }
}

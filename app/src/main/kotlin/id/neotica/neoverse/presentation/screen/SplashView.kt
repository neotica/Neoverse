package id.neotica.neoverse.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import id.neotica.ui.shared.theme.DarkBackground
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SplashView(state: SplashState) {
    Box(
        modifier = Modifier
            .background(DarkBackground)
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {}
}

@Preview
@Composable
private fun SplashViewPreview() {
    SplashView(SplashState())
}

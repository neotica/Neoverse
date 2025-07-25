package id.neotica.alexandria.presentation

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import id.neotica.ui.shared.theme.DarkBackground
import id.neotica.ui.shared.theme.DarkPrimary
import id.neotica.ui.shared.theme.NegativePrimary

@Composable
actual fun NeoverseTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = DarkPrimary,
            onPrimary = Color.White,
            secondary = Color.White,
            onSecondary = DarkPrimary,
            background = DarkBackground,
            onBackground = Color.White,
            tertiary = NegativePrimary,
            onTertiary = Color.White
        )
    ) { content() }
}
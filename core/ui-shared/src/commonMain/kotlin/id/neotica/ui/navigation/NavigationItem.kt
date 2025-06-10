package id.neotica.ui.navigation

import androidx.compose.ui.graphics.painter.Painter

data class NavigationItemNew(
    val title: String,
    val icon: Painter,
    val screen: Any
)
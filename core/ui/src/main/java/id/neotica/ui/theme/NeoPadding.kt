package id.neotica.ui.theme

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp

class NeoPadding {
    companion object {

        val statusBarPadding : Dp
            @Composable
            get() = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

        val systemBarPadding : Dp
            @Composable
            get() = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()
    }

    @Composable
    fun statusBars(): PaddingValues {
        return WindowInsets.statusBars.asPaddingValues()
    }
}
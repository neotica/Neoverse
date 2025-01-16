package id.neotica.ui.navigation

import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController

private val NavController.canGoBack: Boolean
    get() = currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED

fun NavController.navigateBack() {
    if (canGoBack) {
        popBackStack()
    }
}
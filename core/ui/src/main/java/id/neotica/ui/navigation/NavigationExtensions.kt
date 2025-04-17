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

fun NavController.refreshOnBack() {
    this.previousBackStackEntry?.savedStateHandle?.set("refresh", true)
}

fun NavController.onRefresh(block: () -> Unit) {
    val refresh = this.currentBackStackEntry?.savedStateHandle?.get<Boolean>("refresh") == true
    if (refresh) {
        block()
        this.currentBackStackEntry?.savedStateHandle["refresh"] = false
    }
}
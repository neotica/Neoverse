package id.neotica.ui.state

import kotlinx.serialization.Serializable

sealed class ScreenState {
    @Serializable
    data object User: ScreenState()
    @Serializable
    data object Global: ScreenState()
    @Serializable
    data object UserInventory: ScreenState()
}
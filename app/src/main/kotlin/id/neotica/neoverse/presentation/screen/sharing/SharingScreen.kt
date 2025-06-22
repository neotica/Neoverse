package id.neotica.neoverse.presentation.screen.sharing

import kotlinx.serialization.Serializable

@Serializable
sealed class SharingScreen {
    @Serializable
    data object MainSharingView: SharingScreen()
}
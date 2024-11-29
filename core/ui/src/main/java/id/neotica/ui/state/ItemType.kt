package id.neotica.ui.state

import kotlinx.serialization.Serializable

sealed class ItemType {
    @Serializable
    data object Hero: ItemType()
    @Serializable
    data object Item: ItemType()
}
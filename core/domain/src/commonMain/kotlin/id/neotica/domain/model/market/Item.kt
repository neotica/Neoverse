package id.neotica.domain.model.market

import id.neotica.neoverse.getCurrentTimeMillis

data class Item(
    val id: String = "",
    val name: String = "",
    val power: Long = 0,
    val imageUrl: String = "",
    val description: String = "",
    val stock: Int = 0,
    val price: ItemPrice = ItemPrice(),
    val createdAt: Long = getCurrentTimeMillis().time
)

data class ItemPrice(
    val aura: Long = 0,
    val coal: Long = 0
)
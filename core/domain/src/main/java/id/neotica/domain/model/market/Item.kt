package id.neotica.domain.model.market

data class Item(
    val id: String = "",
    val name: String = "",
    val power: Long = 0,
    val imageUrl: String = "",
    val description: String = "",
    val stock: Int = 0,
    val price: ItemPrice = ItemPrice(),
    val createdAt: Long = System.currentTimeMillis()
)

data class ItemPrice(
    val aura: Long = 0,
    val coal: Long = 0
)
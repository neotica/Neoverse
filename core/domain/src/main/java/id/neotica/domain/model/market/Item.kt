package id.neotica.domain.model.market

data class Item(
    val id: String = "",
    val name: String = "",
    val power: Int = 0,
    val imageUrl: String = "",
    val description: String = "",
    val stock: String = "",
    val price: ItemPrice = ItemPrice(),
    val createdAt: Long = System.currentTimeMillis()
)

data class ItemPrice(
    val aura: Int = 0,
    val coal: Int = 0
)
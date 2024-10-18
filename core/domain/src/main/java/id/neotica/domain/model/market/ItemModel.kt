package id.neotica.domain.model.market

data class ItemModel (
    val id: String = "",
    val name: String = "",
    val power: Int = 0,
    val price: Int = 0,
    val ownerId: String = "",
    val ownerName: String = "",
    val imageUrl: String = ""
)
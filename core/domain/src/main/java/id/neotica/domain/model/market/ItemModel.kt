package id.neotica.domain.model.market

data class ItemModel (
    val id: String,
    val name: String,
    val power: Int,
    val owner: String,
    val imageUrl: String
)
package id.neotica.domain.model.market

data class Hero (
    val id: String = "",
    val name: String = "",
    val power: Long = 0,
    val price: Long = 0,
    val owner: OwnerInfo = OwnerInfo(),
    val background: HeroBackground? = HeroBackground(),
    val imageUrl: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

data class OwnerInfo(
    val id: String = "",
    val name: String = ""
)

data class HeroBackground(
    val story: String? = "",
    val origin: String? = "",
    val pantheon: String? = ""
)
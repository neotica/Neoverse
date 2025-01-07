package id.neotica.domain.model.alexandria

import kotlinx.serialization.Serializable

@Serializable
data class Spirits(
    val id: Int,
    val name: String,
    val altName: String? = null,
    val desc: String,
    val imageUrl: String
)
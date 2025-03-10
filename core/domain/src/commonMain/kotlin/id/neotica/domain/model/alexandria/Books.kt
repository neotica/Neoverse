package id.neotica.domain.model.alexandria

import kotlinx.serialization.Serializable

@Serializable
data class Books(
    val id: Int,
    val name: String,
    val url: String,
    val imageUrl: String? = null
)

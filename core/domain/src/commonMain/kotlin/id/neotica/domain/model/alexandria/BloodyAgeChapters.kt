package id.neotica.domain.model.alexandria

import kotlinx.serialization.Serializable

@Serializable
data class BloodyAgeChapters(
    val id: Int,
    val chapter: String,
    val url: String,
)
package id.neotica.domain.model.alexandria

import kotlinx.serialization.Serializable

@Serializable
data class ChapterImage(
    val id: Int,
    val url: String,
    val name: String? = null
)
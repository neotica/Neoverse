package id.neotica.domain.model.alexandria

import kotlinx.serialization.Serializable

@Serializable
data class ChapterDetail(
    val id: Int,
    val content: String,
    val images: List<ChapterImage>? = null
)
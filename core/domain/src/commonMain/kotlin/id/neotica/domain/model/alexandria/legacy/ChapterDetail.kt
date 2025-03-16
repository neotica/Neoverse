package id.neotica.domain.model.alexandria.legacy

import kotlinx.serialization.Serializable

@Serializable
data class ChapterDetail(
    val id: Int,
    val content: String,
    val images: List<ChapterImage>? = null
)

@Serializable
data class ChapterImage(
    val id: Int,
    val url: String,
    val name: String? = null
)
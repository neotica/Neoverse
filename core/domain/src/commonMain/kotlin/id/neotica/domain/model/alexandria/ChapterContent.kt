package id.neotica.domain.model.alexandria

import kotlinx.serialization.Serializable

@Serializable
data class ChapterContent(
    val id: Int,
    val chapterId: Int,
    val content: String,
    val imageUrl: String? = null,
    val createdAt: Long,
    val modifiedAt: Long
)

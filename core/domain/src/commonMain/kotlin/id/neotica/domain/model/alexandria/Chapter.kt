package id.neotica.domain.model.alexandria

import id.neotica.domain.model.alexandria.legacy.ChapterImage
import kotlinx.serialization.Serializable

@Serializable
data class Chapter(
    val id: Int,
    val title: String,
    val bookId: String,
    val createdAt: Long,
    val modifiedAt: Long,
    val image: ChapterImage? = null
)

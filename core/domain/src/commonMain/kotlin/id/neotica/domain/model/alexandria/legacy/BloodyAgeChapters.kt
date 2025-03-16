package id.neotica.domain.model.alexandria.legacy

import kotlinx.serialization.Serializable

@Serializable
data class BloodyAgeChapters(
    val title: String,
    val chapter: List<ChapterItem>,
)

@Serializable
data class ChapterItem(
    val id: Int,
    val chapter: String,
    val url: String,
)
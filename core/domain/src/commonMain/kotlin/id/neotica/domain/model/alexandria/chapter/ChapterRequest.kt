package id.neotica.domain.model.alexandria.chapter

import kotlinx.serialization.Serializable

@Serializable
data class ChapterRequest(
    val title: String,
    val bookId: String
)

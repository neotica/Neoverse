package id.neotica.domain.model.alexandria.book

import kotlinx.serialization.Serializable

@Serializable
data class BookRequest(
    val title: String,
    val author: String,
    val language: String,
    val genre: String? = null,
    val summary: String? = null,
    val isPublished: Boolean,
    val imageUrl: String? = null
)

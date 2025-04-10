package id.neotica.domain.model.alexandria.book

import kotlinx.serialization.Serializable

@Serializable
data class BookRequest(
    val title: String,
    val author: String,
    val imageUrl: String? = null
)

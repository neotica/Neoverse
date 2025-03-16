package id.neotica.domain.model.alexandria

import kotlinx.serialization.Serializable

@Serializable
data class Chapter(
    val id: Int,
    val title: String,
    val bookId: String,
    val createdAt: Long,
    val modifiedAt: Long
)

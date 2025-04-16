package id.neotica.domain.model.alexandria

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Book(
    @SerialName("uuid")
    val id: String,
    val title: String,
    val author: String,
    val language: String,
    val genre: String? = null,
    val summary: String? = null,
    val isPublished: Boolean,
    val imageUrl: String? = null,
    val createdAt: Long,
    val chapters: List<Chapter>? = null
)

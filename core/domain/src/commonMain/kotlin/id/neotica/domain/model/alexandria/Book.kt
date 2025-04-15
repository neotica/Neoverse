package id.neotica.domain.model.alexandria

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Book(
    @SerialName("uuid")
    val id: String,
    val title: String,
    val author: String,
    val isPublished: Boolean,
    val summary: String? = null,
    val imageUrl: String? = null,
    val createdAt: Long,
    val chapters: List<Chapter>? = null
)

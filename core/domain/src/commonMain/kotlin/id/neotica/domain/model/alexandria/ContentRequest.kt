package id.neotica.domain.model.alexandria

import kotlinx.serialization.Serializable

@Serializable
data class ContentRequest(
    val content: String,
    val chapterId: Int
)

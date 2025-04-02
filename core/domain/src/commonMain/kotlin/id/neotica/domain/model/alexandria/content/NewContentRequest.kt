package id.neotica.domain.model.alexandria.content

import kotlinx.serialization.Serializable

@Serializable
data class NewContentRequest(
    val newContent: String
)

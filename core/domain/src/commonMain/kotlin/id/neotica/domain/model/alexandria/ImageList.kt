package id.neotica.model.alexandria.bloodyage

import kotlinx.serialization.Serializable

@Serializable
data class ImageList(
    val id: Int,
    val url: String,
    val name: String? = null
)
package id.neotica.domain.model.alexandria

import id.neotica.model.alexandria.bloodyage.ImageList
import kotlinx.serialization.Serializable

@Serializable
data class BloodyAgeChapters(
    val id: Int,
    val chapter: String,
    val url: String,
    val images: List<ImageList>? = null
)
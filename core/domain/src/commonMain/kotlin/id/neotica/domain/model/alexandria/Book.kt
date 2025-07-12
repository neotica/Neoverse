package id.neotica.domain.model.alexandria

import kotlinx.serialization.Serializable

@Serializable
data class Book(
    val id: String,
    val title: String,
    val author: String = "",
    val language: String,
    val genre: String? = null,
    val summary: String? = null,
    val isPublished: Boolean,
    val imageUrl: String? = null,
    val createdAt: Long,
    //val warning: List<AdministratorWarning>? = null,
    val chapters: List<Chapter>? = null
)

@Serializable
data class AdministratorWarning(
    val id: String,
    val isApproved: Boolean,
    val message: String,
    val version: Int,
    val createdAt: Long,
)
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
    //val rejectionHistory: List<RejectionHistory>? = null,
    val chapters: List<Chapter>? = null
)

// limit all book per user to 20, but if user want to extend should make appeal first.
// limit chapter by 5 per day. or user could appeal.

@Serializable
data class RejectionHistory(
    val id: String,
    val isApproved: Boolean,
    val message: String,
    val version: Int,
    val createdAt: Long,
)
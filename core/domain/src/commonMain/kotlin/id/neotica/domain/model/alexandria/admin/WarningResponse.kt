package id.neotica.domain.model.alexandria.admin

import kotlinx.serialization.Serializable

@Serializable
data class WarningResponse(
    val id: String,
    val bookId: String,
    val bookTitle: String,
    val isAppealed: Boolean,
    val isApproved: Boolean,
    val message: String,
    val createdAt: Long
)

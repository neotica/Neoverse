package id.neotica.domain.pagination

import kotlinx.serialization.Serializable

@Serializable
data class PaginatedContent(
    val id: Int,
    val chapterId: Int,
    val version: Int?,
    val content: String,
    val page: Int,
    val limit: Int,
    val totalPages: Int,
    val hasNext: Boolean,
    val hasPrevious: Boolean
)

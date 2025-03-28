package id.neotica.domain.pagination

import kotlinx.serialization.Serializable

@Serializable
data class Pagination<T>(
    val content: List<T>,
    val currentPage: Int,
    val totalPages: Long,
    val pageSize: Int,
    val hasNextPage: Boolean,
    val hasPreviousPage: Boolean
)


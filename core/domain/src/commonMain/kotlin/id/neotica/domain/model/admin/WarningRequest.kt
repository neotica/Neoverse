package id.neotica.domain.model.admin

import kotlinx.serialization.Serializable

@Serializable
data class WarningRequest(
    val bookId: String? = null,
    val message: String? = null,
    val isAppealed: Boolean = false,
    val isApproved: Boolean = false,
    val version: Int? = null
)
package id.neotica.domain.model.alexandria.admin

import kotlinx.serialization.Serializable

@Serializable
data class WarningRequest(
    val isAppealed: Boolean = false,
    val isApproved: Boolean = false
)

package id.neotica.domain.model.tavern

import id.neotica.neoverse.getCurrentTimeMillis

data class TavernPost(
    val id: String = "",
    val creatorId: String = "",
    val creatorName: String = "",
    val message: String = "",
    val createdAt: Long = getCurrentTimeMillis().time
)

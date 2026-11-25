package id.neotica.auth.data

data class StoredSession(
    val accessToken: String = "",
    val refreshToken: String = "",
    val expiresAtSeconds: Long = 0L,
    val userId: String = "",
    val username: String = ""
)

interface TokenStore {
    fun load(): StoredSession?
    fun save(session: StoredSession)
    fun clear()
}

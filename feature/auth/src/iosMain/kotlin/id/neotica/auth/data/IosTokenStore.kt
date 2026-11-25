package id.neotica.auth.data

import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults

actual fun authPlatformModule(): Module = module {
    single<TokenStore> { IosTokenStore() }
}

private class IosTokenStore : TokenStore {
    private val defaults: NSUserDefaults = NSUserDefaults.standardUserDefaults

    override fun load(): StoredSession? {
        val accessToken = defaults.stringForKey(KEY_ACCESS).orEmpty()
        if (accessToken.isBlank()) return null
        return StoredSession(
            accessToken = accessToken,
            refreshToken = defaults.stringForKey(KEY_REFRESH).orEmpty(),
            expiresAtSeconds = defaults.stringForKey(KEY_EXPIRES)?.toLongOrNull() ?: 0L,
            userId = defaults.stringForKey(KEY_USER_ID).orEmpty(),
            username = defaults.stringForKey(KEY_USERNAME).orEmpty()
        )
    }

    override fun save(session: StoredSession) {
        defaults.setObject(session.accessToken, KEY_ACCESS)
        defaults.setObject(session.refreshToken, KEY_REFRESH)
        defaults.setObject(session.expiresAtSeconds.toString(), KEY_EXPIRES)
        defaults.setObject(session.userId, KEY_USER_ID)
        defaults.setObject(session.username, KEY_USERNAME)
    }

    override fun clear() {
        listOf(KEY_ACCESS, KEY_REFRESH, KEY_EXPIRES, KEY_USER_ID, KEY_USERNAME)
            .forEach(defaults::removeObjectForKey)
    }

    private companion object {
        const val KEY_ACCESS = "access_token"
        const val KEY_REFRESH = "refresh_token"
        const val KEY_EXPIRES = "expires_at"
        const val KEY_USER_ID = "user_id"
        const val KEY_USERNAME = "username"
    }
}

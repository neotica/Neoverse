package id.neotica.auth.data

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun authPlatformModule(): Module = module {
    single<TokenStore> { AndroidTokenStore(androidContext()) }
}

private class AndroidTokenStore(context: Context) : TokenStore {
    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "neoverse_session",
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    override fun load(): StoredSession? {
        val accessToken = prefs.getString(KEY_ACCESS, null).orEmpty()
        if (accessToken.isBlank()) return null
        return StoredSession(
            accessToken = accessToken,
            refreshToken = prefs.getString(KEY_REFRESH, null).orEmpty(),
            expiresAtSeconds = prefs.getLong(KEY_EXPIRES, 0L),
            userId = prefs.getString(KEY_USER_ID, null).orEmpty(),
            username = prefs.getString(KEY_USERNAME, null).orEmpty()
        )
    }

    override fun save(session: StoredSession) {
        prefs.edit()
            .putString(KEY_ACCESS, session.accessToken)
            .putString(KEY_REFRESH, session.refreshToken)
            .putLong(KEY_EXPIRES, session.expiresAtSeconds)
            .putString(KEY_USER_ID, session.userId)
            .putString(KEY_USERNAME, session.username)
            .apply()
    }

    override fun clear() {
        prefs.edit().clear().apply()
    }

    private companion object {
        const val KEY_ACCESS = "access_token"
        const val KEY_REFRESH = "refresh_token"
        const val KEY_EXPIRES = "expires_at"
        const val KEY_USER_ID = "user_id"
        const val KEY_USERNAME = "username"
    }
}

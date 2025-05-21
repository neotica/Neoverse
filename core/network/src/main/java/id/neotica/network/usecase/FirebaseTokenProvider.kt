package id.neotica.network.usecase

import com.google.firebase.auth.FirebaseAuth
import id.neotica.domain.remote.usecase.TokenProvider
import kotlinx.coroutines.tasks.await

class FirebaseTokenProvider: TokenProvider {
    override suspend fun getToken(): String? {
        return FirebaseAuth.getInstance().currentUser?.getIdToken(true)?.await()?.token
    }
}
package id.neotica.neoverse.data.repo

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import id.neotica.neoverse.data.model.User
import id.neotica.neoverse.data.remote.ApiResult
import id.neotica.neoverse.domain.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl: AuthRepository {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override suspend fun login(email: String, password: String): ApiResult<String> {
        auth = FirebaseAuth.getInstance()

        Log.d("repo", "Masuk loginbase")
        val loginResult = auth.signInWithEmailAndPassword(email, password)
        return try {
            loginResult.await()
            if (loginResult.isSuccessful) {
                 ApiResult.Success("Success")
            } else {
                ApiResult.Error("Error")
            }
        } catch (e: Exception) {
            ApiResult.Error("Exception")
        }
    }

    override suspend fun register(username: String, email: String, password: String): ApiResult<String> {
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val registerResult = auth.createUserWithEmailAndPassword(email, password)

        try {
            registerResult.await()
            if (registerResult.isSuccessful) {
                val currentUser = auth.currentUser
                val dataUser = User(
                    userId = currentUser!!.uid,
                    username = username,
                    userEmail = email
                )
                firestore.collection("Users").document(currentUser.uid).set(dataUser)
                return ApiResult.Success("Success")

            } else {
                return ApiResult.Error("Error")
            }
        } catch (e: Exception) {
            return ApiResult.Error("Exception")
        }
    }

    fun loginFlow(email: String, password: String): Flow<ApiResult<String>> = flow {
        auth = FirebaseAuth.getInstance()
        Log.d("repo", "Masuk loginbase")
        val loginResult = auth.signInWithEmailAndPassword(email, password)
        emit(ApiResult.Loading())
        try {
            loginResult.await()
            if (loginResult.isSuccessful) {
                emit(ApiResult.Success("Success"))
            } else {
                emit(ApiResult.Error("Error"))
            }
        } catch (e: Exception) {
            emit(ApiResult.Error("Exception"))
        }
    }.flowOn(Dispatchers.IO)

    override fun logout() {
        auth.signOut()
    }
}
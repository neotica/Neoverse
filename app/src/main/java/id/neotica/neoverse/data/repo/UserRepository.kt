package id.neotica.neoverse.data.repo

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import id.neotica.neoverse.data.model.User
import id.neotica.neoverse.data.remote.ApiResult
import kotlinx.coroutines.tasks.await

class UserRepository {
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    fun isLoggedIn(): Boolean {
        auth = FirebaseAuth.getInstance()
        return auth.currentUser != null
    }

    fun userId(): String {
        return if (isLoggedIn()) auth.currentUser!!.uid else ""
    }

    suspend fun getUserDetail(): ApiResult<User?> {
        firestore = FirebaseFirestore.getInstance()
        return try {
            val snapshot = firestore.collection("Users").document(userId()).get().await()
            val user = snapshot.toObject(User::class.java)
            ApiResult.Success(user!!)
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    suspend fun getUsers(): ApiResult<List<User>> {
        firestore = FirebaseFirestore.getInstance()
        return try {
            val snapshot = firestore.collection("Users").get().await()
            val userList = snapshot.documents.mapNotNull {
                it.toObject(User::class.java)
            }
            ApiResult.Success(userList)
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }
}
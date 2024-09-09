package id.neotica.neoverse.data.remote.repo

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import com.google.firebase.firestore.FirebaseFirestore
import id.neotica.neoverse.data.remote.model.User
import id.neotica.neoverse.data.remote.ApiResult
import id.neotica.neoverse.domain.remote.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl: AuthRepository {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private val firebase = Firebase.database

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
                currentUser.sendEmailVerification()
                firestore.collection("Users").document(currentUser.uid).set(dataUser)
                return ApiResult.Success("Success")

            } else {
                return ApiResult.Error("Error")
            }
        } catch (e: Exception) {
            return ApiResult.Error("Exception")
        }
    }

    override fun deleteAccount() {
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        val currentUser = auth.currentUser
        currentUser!!.delete().addOnCompleteListener {
            Log.d("deleteuser", "usr deleted")
        }
        firestore.collection("Users").document(currentUser.uid).delete()

        val reference = firebase.getReference("userdata").child(currentUser.uid)
        reference.removeValue()
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

    override suspend fun isUsernameExist(username: String): ApiResult<Boolean> {
        firestore = FirebaseFirestore.getInstance()
        return try {
            val snapshot = firestore.collection("Users").get().await()
            val userList = snapshot.documents.mapNotNull {
                it.toObject(User::class.java)
            }
            val checkUser = userList.filter { it.username == username }
            val isUserExist = checkUser[0].username == username
            ApiResult.Success(isUserExist)
        } catch (e: Exception) {
            ApiResult.Success(false)
        }
    }

    override fun logout() {
        auth = FirebaseAuth.getInstance()
        auth.signOut()
    }
}
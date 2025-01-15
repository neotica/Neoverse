package id.neotica.network

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.database.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import id.neotica.domain.ApiResult
import id.neotica.domain.model.User
import id.neotica.domain.remote.AuthRepository
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

        try {
            val loginResult = auth.signInWithEmailAndPassword(email, password).await()
            val user = loginResult.user

            return if (user != null) {
                if (user.isEmailVerified) {
                    ApiResult.Success("Login successful")
                } else {
                    ApiResult.Error("Email not verified. Please verify your email.")
                }
            } else {
                ApiResult.Error("Login failed: User not found.")
            }
        } catch (e: FirebaseAuthInvalidUserException) {
            return ApiResult.Error("Invalid user: ${e.message}")
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            return ApiResult.Error("Invalid credentials: ${e.message}")
        } catch (e: FirebaseAuthException) {
            return ApiResult.Error("Authentication error: ${e.message}")
        } catch (e: Exception) {
            return ApiResult.Error("Unexpected error: ${e.message}")
        }
    }

    override suspend fun register(username: String, email: String, password: String): ApiResult<String> {
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        try {
            val registerResult = auth.createUserWithEmailAndPassword(email, password).await()
            if (registerResult.user != null) {
                val currentUser = auth.currentUser!!
                val dataUser = User(
                    userId = currentUser.uid,
                    username = username,
                    userEmail = email
                )
                // Send verification email
                currentUser.sendEmailVerification()

                // Save user data in Firestore
                firestore.collection("Users").document(currentUser.uid).set(dataUser).await()

                return ApiResult.Success("Registration successful")
            } else {
                return ApiResult.Error("Registration failed: Unknown error")
            }
        } catch (e: FirebaseAuthException) {
            return ApiResult.Error("Authentication Error: ${e.message}")
        } catch (e: FirebaseFirestoreException) {
            return ApiResult.Error("Firestore Error: ${e.message}")
        } catch (e: Exception) {
            return ApiResult.Error("Unexpected Error: ${e.message}")
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

    override fun resetPassword(email: String) {
        auth.sendPasswordResetEmail(email)
    }
}
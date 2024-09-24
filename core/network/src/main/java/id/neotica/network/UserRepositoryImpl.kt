package id.neotica.network

import android.net.Uri
import androidx.core.net.toUri
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import id.neotica.domain.ApiResult
import id.neotica.domain.model.User
import id.neotica.domain.remote.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

class UserRepositoryImpl: UserRepository {
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private val firebase = Firebase.database


    override fun isLoggedIn(): Boolean {
        auth = FirebaseAuth.getInstance()
        return auth.currentUser != null
    }

    override fun isUserVerified(): Boolean {
        auth = FirebaseAuth.getInstance()

        val verified = auth.currentUser?.isEmailVerified
        if (!isLoggedIn()) return false
        return verified!!
    }

    override fun getUserId(): String {
        return if (isLoggedIn()) auth.currentUser!!.uid else ""
    }

    override suspend fun getUserDetail(user: String): ApiResult<User?> {
        firestore = FirebaseFirestore.getInstance()
        return try {
            val snapshot = firestore.collection("Users").document(user).get().await()
            val userValue = snapshot.toObject(User::class.java)
            ApiResult.Success(userValue!!)
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun getAllUsers(): ApiResult<List<User>> {
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

    override fun getFriends(): Flow<ApiResult<List<User>>> = flow {
        val list = mutableListOf<User>()
        emit(ApiResult.Loading())
        try {
            val reference = firebase.getReference("userdata").child(getUserId()).child("friends").get().await()
            reference.children.forEach { data ->
                val userData = data.getValue(User::class.java)

                if (data != null) {
                    list.add(userData!!)
                    emit(ApiResult.Success(list))
                } else {
                    emit(ApiResult.Error("data null"))
                }
            }
        } catch (e: Exception) {
            emit(ApiResult.Error("exception: ${e.message}"))
        }
    }

    override suspend fun searchUser(search: String): ApiResult<List<User>> {
        firestore = FirebaseFirestore.getInstance()
        return try {
            val snapshot = firestore.collection("Users").get().await()
            val userList = snapshot.documents.mapNotNull {
                it.toObject(User::class.java)
            }
            val userFiltered = userList.filter { it.username == search }
            ApiResult.Success(userFiltered)
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun addFriend(friend: User) {
        val original = firebase.getReference("userdata")
            .child(getUserId()).child("friends")
            .child(friend.userId).setValue(friend)
        original.await()
    }

    override suspend fun unfriend(friend: User) {
        val original = firebase.getReference("userdata")
            .child(getUserId()).child("friends")
            .child(friend.userId).removeValue()
        original.await()
    }

    override suspend fun changeUserProfile(user: User) {
        firestore.collection("Users").document(getUserId()).set(user)
    }

    @OptIn(InternalCoroutinesApi::class)
    override suspend fun getUserProfilePic(): String = withContext(Dispatchers.IO) {
        val storageRef = FirebaseStorage.getInstance().reference
            .child("profilePicture/${getUserId()}")

        suspendCancellableCoroutine { cont ->
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                cont.resume(uri.toString())
            }.addOnFailureListener { exc ->
                cont.tryResumeWithException(exc)
            }
        }
    }

    override suspend fun changeProfilePicture(uri: String) {
        val storageRef = FirebaseStorage.getInstance().reference
        storageRef.child("profilePicture/${getUserId()}").putFile(uri.toUri())
    }
}
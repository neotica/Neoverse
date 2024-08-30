package id.neotica.neoverse.data.repo

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import com.google.firebase.firestore.FirebaseFirestore
import id.neotica.neoverse.data.model.Friends
import id.neotica.neoverse.data.model.User
import id.neotica.neoverse.data.remote.ApiResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class UserRepository {
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private val firebase = Firebase.database


    private fun isLoggedIn(): Boolean {
        auth = FirebaseAuth.getInstance()
        return auth.currentUser != null
    }

    fun userId(): String {
        return if (isLoggedIn()) auth.currentUser!!.uid else ""
    }

    suspend fun getUserDetail(user: String = userId()): ApiResult<User?> {
        firestore = FirebaseFirestore.getInstance()
        return try {
            val snapshot = firestore.collection("Users").document(user).get().await()
            val userValue = snapshot.toObject(User::class.java)
            ApiResult.Success(userValue!!)
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

    suspend fun getFriends(): Flow<ApiResult<List<Friends>>> = flow {
        val list = mutableListOf<Friends>()
        emit(ApiResult.Loading())
        try {
            val reference = firebase.getReference("userdata").child(userId()).child("friends").get().await()
            reference.children.forEach { data ->
                val userData = data.getValue(Friends::class.java)

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

    suspend fun getFriends2(searchName: String): ApiResult<List<User>> {
        firestore = FirebaseFirestore.getInstance()
        return try {
            val snapshot = firestore.collection("Users").get().await()
            val userList = snapshot.documents.mapNotNull {
                it.toObject(User::class.java)
            }
            val userFiltered = userList.filter { it.username == searchName }
            ApiResult.Success(userFiltered)
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    suspend fun searchUser(searchName: String): ApiResult<List<User>> {
        firestore = FirebaseFirestore.getInstance()
        return try {
            val snapshot = firestore.collection("Users").get().await()
            val userList = snapshot.documents.mapNotNull {
                it.toObject(User::class.java)
            }
            val userFiltered = userList.filter { it.username == searchName }
            ApiResult.Success(userFiltered)
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    suspend fun addFriend(friend: Friends) {
        val original = firebase.getReference("userdata")
            .child(userId()).child("friends")
            .child(friend.id).setValue(friend)
        original.await()
    }

}
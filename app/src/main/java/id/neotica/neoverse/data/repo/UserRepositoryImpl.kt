package id.neotica.neoverse.data.repo

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import com.google.firebase.firestore.FirebaseFirestore
import id.neotica.neoverse.data.model.ChatRoom
import id.neotica.neoverse.data.model.Friends
import id.neotica.neoverse.data.model.ParentNeo
import id.neotica.neoverse.data.model.User
import id.neotica.neoverse.data.remote.ApiResult
import id.neotica.neoverse.domain.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

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

    override fun getFriends(): Flow<ApiResult<List<Friends>>> = flow {
        val list = mutableListOf<Friends>()
        emit(ApiResult.Loading())
        try {
            val reference = firebase.getReference("userdata").child(getUserId()).child("friends").get().await()
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

    suspend fun getChatroomFriend(): Flow<ApiResult<List<ParentNeo<ChatRoom>>>> = flow {
        val list = mutableListOf<ParentNeo<ChatRoom>>()
        emit(ApiResult.Loading())
        try {
            val reference = firebase.getReference("chatroom").get().await()
            reference.children.forEach { data ->
                val cr = data.getValue(ChatRoom::class.java)
                val channel = ParentNeo(
                    key = data.key!!,
                    value = cr!!
                )
                list.add(channel)
                if (data != null) {
                    emit(ApiResult.Success(list.sortedByDescending {

                        it.value.lastOpened }.filter { it == it
                        }))
                } else {
                    emit(ApiResult.Error("data null"))
                }
            }
        } catch (e: Exception) {
            emit(ApiResult.Error("exception: ${e.message}"))
        }
    }

}
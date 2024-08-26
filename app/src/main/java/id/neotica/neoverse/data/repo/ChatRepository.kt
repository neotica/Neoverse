package id.neotica.neoverse.data.repo

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import id.neotica.neoverse.data.model.ChatRoom
import id.neotica.neoverse.data.remote.ApiResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class ChatRepository {
    private lateinit var auth: FirebaseAuth

    private val firebase = Firebase.database

    suspend fun getChatroom(): Flow<ApiResult<List<ChatRoom>>> = flow {
        val list = mutableListOf<ChatRoom>()
        emit(ApiResult.Loading())
        try {
            val reference = firebase.getReference("chatroom").get().await()
            reference.children.forEach { data ->
                val channel = ChatRoom(id = data.key!!, name = data.value.toString())
                list.add(channel)
            }
            emit(ApiResult.Success(list))
        } catch (e: Exception) {
            emit(ApiResult.Error(e.message))
        }
    }

    suspend fun addChatroom(name: String) {
        val key = firebase.getReference("chatroom").push().key
        val reference = firebase.getReference("chatroom").child(key!!).setValue(name)
        reference.await()
        if (reference.isSuccessful) getChatroom()
    }
}
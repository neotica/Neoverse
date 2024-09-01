package id.neotica.neoverse.data.repo

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import id.neotica.neoverse.data.model.ChatRoom
import id.neotica.neoverse.data.model.Message
import id.neotica.neoverse.data.model.ParentNeo
import id.neotica.neoverse.data.remote.ApiResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class ChatRepository {
    private lateinit var auth: FirebaseAuth

    private val firebase = Firebase.database

    suspend fun getChatroom(): Flow<ApiResult<List<ParentNeo<ChatRoom>>>> = flow {
        val list = mutableListOf<ParentNeo<ChatRoom>>()
        try {
            val reference = firebase.getReference("chatroom").get().await()
            reference.children.forEach { data ->
                val cr = data.getValue(ChatRoom::class.java)
                val channel = ParentNeo(
                    key = data.key!!,
                    value = cr!!
                )
                list.add(channel)
                if (data.value != null) {
                    emit(ApiResult.Success(list.sortedByDescending { it.value.lastOpened }))
                } else {
                    emit(ApiResult.Error("data null"))
                }
            }
        } catch (e: NullPointerException) {
            emit(ApiResult.Error("exception: ${e.message}"))
        }
    }

    suspend fun addChatroom(name: ChatRoom) {
        val key = firebase.getReference("chatroom").push().key
        val reference = firebase.getReference("chatroom").child(key!!).setValue(name)
        reference.await()
        if (reference.isSuccessful) getChatroom()
    }

    fun sendMessage(chatId: String, message: Message) {
        firebase.getReference("messages").child(chatId).push().setValue(message)
    }

    suspend fun readMessage(chatId: String, chatRoom: ChatRoom) {
        val key = firebase.getReference("chatroom").child(chatId).setValue(chatRoom)
        key.await()
    }
}
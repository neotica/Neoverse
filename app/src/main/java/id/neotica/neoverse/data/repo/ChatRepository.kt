package id.neotica.neoverse.data.repo

import android.util.Log
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
                    emit(ApiResult.Success(list.sortedByDescending { it.value.lastOpened }))
                } else {
                    emit(ApiResult.Error("data null"))
                }
            }
        } catch (e: Exception) {
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
        Log.d("sendMessage", message.toString())
        firebase.getReference("messages").child(chatId).push().setValue(message)
    }

    suspend fun readMessage(chatId: String, chatRoom: ChatRoom) {
        val key = firebase.getReference("chatroom").child(chatId).setValue(chatRoom)
        key.await()
    }

    suspend fun sendMessageRead(chatId: String, message: Message, parent: ParentNeo<Message>) {
        Log.d("sendMessage", message.toString())
        Log.d("sendMessage", "chatId: $chatId")

//        firebase.getReference("messages").child(chatId).child(message.id).setValue(message).await()
    }

    suspend fun addChatroomRead(name: ChatRoom) {
//        val key = firebase.getReference("chatroom").push().key
        val reference = firebase.getReference("chatroom").child(name.id).setValue(name)
        reference.await()
        if (reference.isSuccessful) getChatroom()
    }


}
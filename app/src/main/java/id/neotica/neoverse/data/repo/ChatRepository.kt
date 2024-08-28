package id.neotica.neoverse.data.repo

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import id.neotica.neoverse.data.model.ChatRoom
import id.neotica.neoverse.data.model.Message
import id.neotica.neoverse.data.model.ParentChatRoom
import id.neotica.neoverse.data.remote.ApiResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class ChatRepository {
    private lateinit var auth: FirebaseAuth

    private val firebase = Firebase.database

    suspend fun getChatroom(): Flow<ApiResult<List<ParentChatRoom>>> = flow {
        val list = mutableListOf<ParentChatRoom>()
        emit(ApiResult.Loading())
        try {
            val reference = firebase.getReference("chatroom").get().await()
            reference.children.forEach { data ->
                Log.d("chatRepo", "getParentChatroom snap: $data")

                val cr = data.getValue(ChatRoom::class.java)

                val channel = ParentChatRoom(
                    key = data.key!!,
                    value = cr!!
                )
                list.add(channel)
                if (data != null) {
                    emit(ApiResult.Success(list))
                } else {
                    emit(ApiResult.Error("data null"))
                }
                Log.d("chatRepo", "channel: ${channel.value}")

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
        firebase.getReference("messages").child(chatId).push().setValue(message)
    }
}
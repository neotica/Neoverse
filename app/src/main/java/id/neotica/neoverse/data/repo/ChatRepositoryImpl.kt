package id.neotica.neoverse.data.repo

import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import id.neotica.neoverse.data.model.ChatRoom
import id.neotica.neoverse.data.model.Message
import id.neotica.neoverse.data.model.ParentNeo
import id.neotica.neoverse.data.remote.ApiResult
import id.neotica.neoverse.domain.ChatRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class ChatRepositoryImpl: ChatRepository {
    private val firebase = Firebase.database

    override suspend fun getChatroom(): Flow<ApiResult<List<ParentNeo<ChatRoom>>>> = flow {
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

    override suspend fun addChatroom(name: ChatRoom) {
        val key = firebase.getReference("chatroom").push().key
        val reference = firebase.getReference("chatroom").child(key!!).setValue(name)
        reference.await()
        if (reference.isSuccessful) getChatroom()
    }

    override fun getMessages(chatId: String): Flow<List<Message>> = callbackFlow {
        val reqDb = firebase.getReference("messages").child(chatId).orderByChild("createdAt")

        val event = reqDb.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list2 = snapshot.children.mapNotNull { data ->
                    val message = data.getValue(Message::class.java)
                    message
                }
                trySend(list2)
            }

            override fun onCancelled(error: DatabaseError) {
                close()
            }
        })
        awaitClose {
            reqDb.removeEventListener(event)
        }
    }

    override fun sendMessage(chatId: String, message: Message) {
        firebase.getReference("messages").child(chatId).push().setValue(message)
    }

    override suspend fun readMessage(chatId: String, chatRoom: ChatRoom) {
        val key = firebase.getReference("chatroom").child(chatId).setValue(chatRoom)
        key.await()
    }
}
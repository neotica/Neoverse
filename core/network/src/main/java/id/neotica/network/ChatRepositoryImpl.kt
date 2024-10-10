package id.neotica.network

import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import id.neotica.domain.ApiResult
import id.neotica.domain.model.ChatRoom
import id.neotica.domain.model.Message
import id.neotica.domain.model.ParentNeo
import id.neotica.domain.remote.ChatRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class ChatRepositoryImpl: ChatRepository {
    private val firebase = Firebase.database

    override suspend fun getAllChatrooms(): Flow<ApiResult<List<ParentNeo<ChatRoom>>>> = flow {
        val list = mutableListOf<ParentNeo<ChatRoom>>()
        list.clear()
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

    override suspend fun getChatroom(id: String): ApiResult<ChatRoom> {
        val reference = firebase.getReference("chatroom").child(id).get().await()
        return try {
            val value = reference.getValue(ChatRoom::class.java)
            ApiResult.Success(value!!)
        } catch (e: NullPointerException) {
            ApiResult.Error("exception: ${e.message}")
        }
    }


    override suspend fun addChatroom(name: ChatRoom) {
        val key = firebase.getReference("chatroom").push().key
        val chatroom = ChatRoom(
            id = key.toString(),
            name = name.name,
            user1 = name.user1,
            user2 = name.user2,
            user1id = name.user1id,
            user2id = name.user2id,
            user1Read = name.user1Read,
            user2Read = name.user2Read
        )
        val reference = firebase.getReference("chatroom").child(key!!).setValue(chatroom)
        reference.await()
        if (reference.isSuccessful) getAllChatrooms()
    }

    override suspend fun deleteChatroom(chatroom: ChatRoom) {
        val reference = firebase.getReference("chatroom").child(chatroom.id).removeValue()
        reference.await()
        val messages = firebase.getReference("messages").child(chatroom.name).removeValue()
        messages.await()
        if (reference.isSuccessful && messages.isSuccessful) getAllChatrooms()
    }

    override fun getMessages(chatId: String): Flow<List<Message>> = callbackFlow {
        val reqDb = firebase.getReference("messages").child(chatId)
            .orderByChild("createdAt")

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

    override suspend fun updateChatroom(chatId: String, chatRoom: ChatRoom) {
        val key = firebase.getReference("chatroom").child(chatId).setValue(chatRoom)
        key.await()
    }
}
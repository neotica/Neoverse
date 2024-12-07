package id.neotica.network

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.messaging.messaging
import id.neotica.domain.ApiResult
import id.neotica.domain.model.chat.ChatRoom
import id.neotica.domain.model.chat.Message
import id.neotica.domain.model.chat.ParentNeo
import id.neotica.domain.model.chat.notification.ChatState
import id.neotica.domain.model.chat.notification.SendMessageDto
import id.neotica.domain.remote.ChatRepository
import id.neotica.network.di.NetworkApiService
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create

class ChatRepositoryImpl: ChatRepository {
    private val firebase = Firebase.database

    private var state by mutableStateOf(ChatState())

    private fun onRemoteTokenChange(newToken: String) {
        state = state.copy(
            remoteToken = newToken
        )
    }

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


    override suspend fun addChatroom(chatRoom: ChatRoom) {
        val reference = firebase.getReference("chatroom").child(chatRoom.id).setValue(chatRoom)
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
        val token = reloadFcmToken()
        val chatRoomCustom = chatRoom.copy(fcmToken = token)

        val key = firebase.getReference("chatroom").child(chatId).setValue(chatRoomCustom)
        key.await()
    }

    private val api: NetworkApiService = Retrofit.Builder()
        .baseUrl("http://18.141.159.232:8080")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
        .create()

    private suspend fun reloadFcmToken(): String {
        return Firebase.messaging.token.await().also { newToken ->
            // Update the state or save the token if needed
            onRemoteTokenChange(newToken)
            Log.d("FCM Token", "Reloaded token: $newToken")
        }
    }

    override suspend fun subscribeToTopic(topic: String) {
        Firebase.messaging.subscribeToTopic(topic).await()
    }

    override suspend fun sendNotification(isBroadcast: Boolean, messageDto: SendMessageDto) {
        try {
            reloadFcmToken()
            if (isBroadcast) {
                api.sendMessage(messageDto)
            } else api.broadcast(messageDto)
        } catch (e: Exception) {

            Log.d("sendNotif", e.message.toString())
        }

    }
}
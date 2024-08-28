package id.neotica.neoverse.presentation.screen.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import id.neotica.neoverse.data.model.Message
import id.neotica.neoverse.data.remote.ApiResult
import id.neotica.neoverse.data.repo.ChatRepository
import id.neotica.neoverse.data.repo.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class ChatViewModel(
    private val userRepo: UserRepository,
    private val chatRepo: ChatRepository
): ViewModel() {
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val message = _messages.asStateFlow()
    private val _messages1 = MutableStateFlow<ApiResult<List<Message>>>(ApiResult.Loading())
    val message1 = _messages1.asStateFlow()
    private val db = Firebase.database

    fun sendMessage(chatId: String, chat: String) {
        val messageValue = Message(
            id = db.reference.push().key ?: UUID.randomUUID().toString(),
            senderId = userRepo.userId(),
            receiverId = Firebase.auth.currentUser?.uid ?: "",
            message = chat,
            createdAt = System.currentTimeMillis(),
            senderImage = null,
            imageUrl = null
        )

        chatRepo.sendMessage(chatId, messageValue)
    }

    fun getMessages(chatId: String) {
        db.getReference("messages").child(chatId).orderByChild("createdAt")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<Message>()
                    snapshot.children.forEach { data ->
                        val message = data.getValue(Message::class.java)
                        message?.let {
                            list.add(it)
                        }
                    }
                    _messages.value = list
                }

                override fun onCancelled(error: DatabaseError) {
                    //hello
                }
            })
    }
}
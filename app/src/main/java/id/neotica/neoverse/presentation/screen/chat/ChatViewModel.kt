package id.neotica.neoverse.presentation.screen.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import id.neotica.neoverse.data.model.Message
import id.neotica.neoverse.data.model.ParentNeo
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
    private val _parentMessage = MutableStateFlow<List<ParentNeo<Message>>>(emptyList())
    val parentMessage = _parentMessage.asStateFlow()
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

    fun readChat(chatId: String, message: Message, parent: ParentNeo<Message>) = viewModelScope.launch {
        val messageList = mutableListOf<Message>()
        val messageValue = Message(
            id = message.id, //value.id,
            senderId = message.senderId, //value.senderId,
            receiverId = message.receiverId, //value.senderId,
            message = message.message, //.value.message,
            createdAt = message.createdAt, //.value.createdAt,
            read = true,
            senderImage = message.senderImage, //.value.senderImage,
            imageUrl = message.imageUrl //.value.imageUrl
        )
        messageList.add(messageValue)
//        chatRepo.sendMessageRead(chatId, messageValue)

    }

    fun getMessages(chatId: String) {
        db.getReference("messages").child(chatId).orderByChild("createdAt")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<Message>()
                    val listParent = mutableListOf<ParentNeo<Message>>()
                    snapshot.children.forEach { data ->
                        val message = data.getValue(Message::class.java)
//                        val parentNeo = ParentNeo<Message>()
//                        val parentmsg = data.getValue(ParentMessage::class.java)
                        message?.let {
                            list.add(it)
                        }
                    }
                    _messages.value = list
//                    _parentMessage.value = listParent
                }

                override fun onCancelled(error: DatabaseError) {
                    //hello
                }
            })
    }
}
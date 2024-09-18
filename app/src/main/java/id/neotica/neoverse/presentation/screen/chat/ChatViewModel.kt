package id.neotica.neoverse.presentation.screen.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import id.neotica.domain.model.Message
import id.neotica.domain.remote.ChatRepository
import id.neotica.domain.remote.UserRepository
import id.neotica.ui.navigation.Screen
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import java.util.UUID

class ChatViewModel(
    private val userRepo: UserRepository,
    private val chatRepo: ChatRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val args = savedStateHandle.toRoute<Screen.Chat>()
    val message = chatRepo.getMessages(args.chatId).stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    private val db = Firebase.database

    fun sendMessage(chatId: String, chat: String) {
        val messageValue = Message(
            id = db.reference.push().key ?: UUID.randomUUID().toString(),
            senderId = userRepo.getUserId(),
            receiverId = Firebase.auth.currentUser?.uid ?: "",
            message = chat,
            createdAt = System.currentTimeMillis(),
            senderImage = null,
            imageUrl = null
        )

        chatRepo.sendMessage(chatId, messageValue)
    }
}
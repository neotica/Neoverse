package id.neotica.home.addchat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.domain.ApiResult
import id.neotica.domain.model.chat.Conversation
import id.neotica.domain.remote.ConversationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddChatViewModel(
    private val conversationRepo: ConversationRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<ApiResult<Conversation>?>(null)
    val state = _state.asStateFlow()

    fun startChat(username: String) = viewModelScope.launch {
        val target = username.trim()
        if (target.isEmpty()) return@launch
        _state.value = ApiResult.Loading()
        _state.value = conversationRepo.getOrCreateConversation(target)
    }

    fun consumeState() {
        _state.value = null
    }
}

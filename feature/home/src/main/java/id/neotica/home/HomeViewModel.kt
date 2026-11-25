package id.neotica.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.auth.domain.AuthRepository
import id.neotica.domain.ApiResult
import id.neotica.domain.model.chat.Conversation
import id.neotica.domain.remote.ConversationRepository
import id.neotica.domain.session.SessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val authRepo: AuthRepository,
    private val conversationRepo: ConversationRepository,
    private val session: SessionRepository,
) : ViewModel() {

    private val _conversations = MutableStateFlow<ApiResult<List<Conversation>>>(ApiResult.Loading())
    val conversations = _conversations.asStateFlow()

    val username: String = session.getUsername().orEmpty()

    fun isLoggedIn(): Boolean = authRepo.isLoggedIn()

    fun loadConversations() = viewModelScope.launch {
        _conversations.value = ApiResult.Loading()
        _conversations.value = conversationRepo.getConversations()
    }

    fun deleteConversation(conversationId: String) = viewModelScope.launch {
        conversationRepo.deleteConversation(conversationId)
        loadConversations()
    }
}

package id.neotica.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.auth.domain.AuthRepository
import id.neotica.domain.ApiResult
import id.neotica.domain.remote.ConversationRepository
import id.neotica.domain.session.SessionRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val authRepo: AuthRepository,
    private val conversationRepo: ConversationRepository,
    session: SessionRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(
        HomeState(username = session.getUsername().orEmpty())
    )
    val state = _state.asStateFlow()

    private val _events = Channel<HomeEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        if (!authRepo.isLoggedIn()) {
            viewModelScope.launch { _events.send(HomeEvent.RequireLogin) }
        }
    }

    fun onQueryChange(value: String) {
        _state.update { it.copy(query = value) }
    }

    fun loadConversations() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, error = null) }
        when (val result = conversationRepo.getConversations()) {
            is ApiResult.Success -> _state.update {
                it.copy(conversations = result.data.orEmpty(), isLoading = false)
            }
            is ApiResult.Error -> _state.update {
                it.copy(isLoading = false, error = result.errorMessage ?: "Unable to load chats")
            }
            is ApiResult.Loading -> Unit
        }
    }
}

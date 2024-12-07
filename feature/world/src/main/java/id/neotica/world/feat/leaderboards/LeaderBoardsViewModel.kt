package id.neotica.world.feat.leaderboards

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.domain.ApiResult
import id.neotica.domain.model.UserData
import id.neotica.domain.model.chat.ParentNeo
import id.neotica.domain.remote.FeedRepository
import id.neotica.domain.usecase.GetUserIdUseCase
import id.neotica.domain.usecase.GetUserNameUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LeaderBoardsViewModel(
    private val getUserIdUseCase: GetUserIdUseCase,
    private val feedRepository: FeedRepository,
    private val getUserNameUseCase: GetUserNameUseCase,
): ViewModel() {
    private fun currentUsername(): String = getUserNameUseCase.invoke()
    private fun currentUserId(): String = getUserIdUseCase.invoke()

    private val _userData: MutableStateFlow<ApiResult<UserData>> = MutableStateFlow(ApiResult.Loading())

    private val _allUser: MutableStateFlow<ApiResult<List<ParentNeo<UserData>>>> = MutableStateFlow(ApiResult.Loading())
    val allUser = _allUser.asStateFlow()



    init {
        getAllUser()
    }

    private fun getAllUser() = viewModelScope.launch {
        feedRepository.getAllUserData().collect {
            _allUser.value = it
        }
    }

    fun hasJoinedLeaderBoards(): Boolean {
        val filteredValue = _allUser.value.data?.filter { it.value.world.username == currentUsername() }
        Log.d("leaderboard", filteredValue.toString())
        return !filteredValue.isNullOrEmpty()
    }

    fun joinLeaderboards() = viewModelScope.launch {
        feedRepository.inputUsernameToWorld(currentUsername(), currentUserId())
        getAllUser()
    }
}
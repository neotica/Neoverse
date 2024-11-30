package id.neotica.world.feat.leaderboards

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.domain.ApiResult
import id.neotica.domain.model.UserData
import id.neotica.domain.model.chat.ParentNeo
import id.neotica.domain.remote.FeedRepository
import id.neotica.domain.remote.UserRepository
import id.neotica.domain.usecase.GetUserIdUseCase
import id.neotica.domain.usecase.GetUserNameUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LeaderBoardsViewModel(
    private val getUserIdUseCase: GetUserIdUseCase,
    private val feedRepository: FeedRepository,
    private val getUserNameUseCase: GetUserNameUseCase,
    private val useRepo: UserRepository
): ViewModel() {
    private fun currentUsername(): String = getUserNameUseCase.invoke()
    private fun currentUserId(): String = getUserIdUseCase.invoke()

    private val _userData: MutableStateFlow<ApiResult<UserData>> = MutableStateFlow(ApiResult.Loading())

    private val _allUser: MutableStateFlow<ApiResult<List<ParentNeo<UserData>>>> = MutableStateFlow(ApiResult.Loading())
    val allUser = _allUser.asStateFlow()



    init {
        getAllUser()
        fetchUserWorld()
    }

    private fun getAllUser() = viewModelScope.launch {
        feedRepository.getAllUserData().collect {
            _allUser.value = it
            Log.d("leaderboards", it.toString())
        }
    }

    private fun fetchUserWorld() = viewModelScope.launch {
        useRepo.getWorldDetail(currentUserId()).collect {
            _userData.value = it
        }
    }

    fun inputUserIdToWorld() = viewModelScope.launch {
        _userData.value.data?.let { data ->

            val dummyData = data.copy(
                world = data.world.copy(username = currentUsername())
            )
            feedRepository.inputUsernameToWorld(dummyData)
        }
    }
}
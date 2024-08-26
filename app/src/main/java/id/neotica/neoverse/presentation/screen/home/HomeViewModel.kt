package id.neotica.neoverse.presentation.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.database.database
import id.neotica.neoverse.data.model.ChatRoom
import id.neotica.neoverse.data.model.User
import id.neotica.neoverse.data.remote.ApiResult
import id.neotica.neoverse.data.repo.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val userRepo: UserRepository
): ViewModel() {

    private var _userList: MutableStateFlow<ApiResult<List<User>>> = MutableStateFlow(ApiResult.Loading())
    val userList = _userList.asStateFlow()

    private var _user: MutableStateFlow<ApiResult<User?>> = MutableStateFlow(ApiResult.Loading())
    val user = _user.asStateFlow()

    private val _chatroom = MutableStateFlow<List<ChatRoom>>(emptyList())
    val chatroom = _chatroom.asStateFlow()

    private val firebaseDatabase = Firebase.database

    init {
//        getChannels()
    }

    fun currentUser(): String = userRepo.userId()

    fun currentUserName(): String = _user.value.data?.userId!!

    fun getUser() = viewModelScope.launch {
        val user = userRepo.getUserDetail()
        _user.value = ApiResult.Loading()
        _user.value = user
    }

    fun getUsers() = viewModelScope.launch {
        val user = userRepo.getUsers()
        _userList.value = ApiResult.Loading()
        _userList.value = user
    }

    fun matchingChatroom(secondUser: String) {

    }

    fun getChatroom() {
        firebaseDatabase.getReference("chatroom").get().addOnSuccessListener {
            val list = mutableListOf<ChatRoom>()
            it.children.forEach { data ->
                val channel = ChatRoom(id = data.key!!, name = data.value.toString())
                list.add(channel)
            }
            _chatroom.value = list
        }
    }

    fun addChatroom(name: String, owner: String) {
        val key = firebaseDatabase.getReference("chatroom").push().key
        firebaseDatabase.getReference("chatroom").child(key!!).setValue(name).addOnSuccessListener {
            getChatroom()
        }
    }
}
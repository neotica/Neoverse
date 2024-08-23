package id.neotica.neoverse.presentation.screen.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.neotica.neoverse.data.model.User
import id.neotica.neoverse.data.repo.UserRepository

class HomeViewModel(
    private val userRepo: UserRepository
): ViewModel() {
    val name = MutableLiveData<String>()
    val imageUrl = MutableLiveData<String>()
    val message = MutableLiveData<String>()

    fun getUsers(): LiveData<List<User>> {
        return userRepo.getUsers()
    }
}
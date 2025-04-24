package id.neotica.alexandria.screen.admin

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.domain.ApiResult
import id.neotica.domain.model.alexandria.Book
import id.neotica.domain.pagination.Pagination
import id.neotica.domain.remote.AlexandriaAdminRepository
import id.neotica.domain.remote.PermissionRepository
import id.neotica.domain.usecase.GetUserNameUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LibraryAdminViewModel(
    private val adminRepo: AlexandriaAdminRepository,
    private val permissionRepository: PermissionRepository,
    private val getUserNameUseCase: GetUserNameUseCase
): ViewModel() {
    private var _booksOld: MutableStateFlow<ApiResult<Pagination<Book>>> = MutableStateFlow(
        ApiResult.Loading())
    val books = _booksOld.asStateFlow()

    private var _permission: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val permission = _permission.asStateFlow()

    fun userName() = getUserNameUseCase.invoke()

    init {
        getBooks()
        getPermission()
    }

    private fun getBooks() = viewModelScope.launch {
        adminRepo.getBooks().collect {
            _booksOld.value = it
        }
    }

    fun getPermission() = viewModelScope.launch {
        permissionRepository.getPermissionList().collect {
            if (it is ApiResult.Success) {
                _permission.value = it.data?.contains(userName()) == true
                Log.d("permissionList", it.toString())
//                Log.d("permissionList2", it.data!!.contains("laetuz") .toString())
//                Log.d("permissionList3", userName() .toString())
//                Log.d("permission", _permission.value.toString())
            }
        }
    }

    suspend fun hasAuthorPermission(): Boolean {
        return permissionRepository.hasPermission(userName())
    }
}
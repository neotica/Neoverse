package id.neotica.neoverse.presentation.screen.camerax

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.domain.ApiResult
import id.neotica.domain.local.CurrentUserRepository
import id.neotica.domain.model.User
import id.neotica.domain.remote.UserRepository
import id.neotica.neoverse.utils.compressUri
import id.neotica.neoverse.utils.getImageUriFromBitMap
import id.neotica.neoverse.utils.resizeBitmap
import id.neotica.neoverse.utils.rotateBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.Executor

class CameraPreviewViewModel(
    private val userRepo: UserRepository,
    private val currentUserDatabase: CurrentUserRepository
): ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _cameraState: MutableStateFlow<ApiResult<String>> = MutableStateFlow(ApiResult.Loading())
    val cameraState = _cameraState.asStateFlow()

    private var _user: MutableStateFlow<ApiResult<User?>> = MutableStateFlow(ApiResult.Loading())

    fun capturePhoto(
        context: Context,
        cameraController: LifecycleCameraController,
    ) {
        val mainExecutor: Executor = ContextCompat.getMainExecutor(context)

        _isLoading.value = true

        cameraController.takePicture(mainExecutor, object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                viewModelScope.launch(Dispatchers.IO) {
                    val correctedBitmap: Bitmap = image
                        .toBitmap()
                        .rotateBitmap(image.imageInfo.rotationDegrees)

                    val resizedBitmap = resizeBitmap(correctedBitmap, 800, 800)

                    val photoUri = getImageUriFromBitMap(resizedBitmap, context)
                    val uriComplete = compressUri(photoUri, context)
                    changeProfilePic(uriComplete.toString())
                    image.close()
                }
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("CaptureViewModel", "Error capturing image", exception)
                _isLoading.value = false
            }
        })
    }

    fun changeProfilePic(uri: String) = viewModelScope.launch {
        try {
            _cameraState.value = ApiResult.Loading()

            getUser()
            userRepo.changeProfilePicture(uri)

            currentUserDatabase.getUser().collect { userList ->
                val db = userList.firstOrNull() ?: return@collect
                val updatedUser = User(
                    userId = db.userId,
                    username = db.username,
                    userEmail = db.userEmail,
                    status = db.status,
                    imageUrl = userRepo.getUserProfilePic()
                )

                userRepo.changeUserProfile(updatedUser)
                currentUserDatabase.saveUser(updatedUser)
                getUser()
                _cameraState.value = ApiResult.Success(uri)
            }
        } catch (e: Exception) {
            Log.e("CaptureViewModel", "Error changing profile picture", e)
            _cameraState.value = ApiResult.Error(e.toString())
        }
    }

    private fun getUser() = viewModelScope.launch {
        val user = userRepo.getUserDetail(userRepo.getUserId())
        _user.value = ApiResult.Loading()
        _user.value = user
        user.data?.let { currentUserDatabase.saveUser(it) }
    }

}
package id.neotica.neoverse.presentation.screen.camerax

import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import id.neotica.camera.R
import id.neotica.domain.ApiResult
import id.neotica.ui.shared.components.BasicScaffold
import id.neotica.ui.shared.components.LoadingIndicator
import id.neotica.ui.shared.navigation.navigateBack
import id.neotica.ui.shared.theme.NeoPadding
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CameraPreviewView(
    navController: NavController,
    viewModel: CameraPreviewViewModel = koinViewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val cameraController: LifecycleCameraController =
        remember { LifecycleCameraController(context) }

    var launchKey by remember { mutableStateOf(0) }
    var isPressed by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    cameraController.cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

    LaunchedEffect(launchKey) {
        viewModel.cameraState.collect { result ->
            Log.d("cameraState", result.toString())
            when(result) {
                is ApiResult.Loading ->{
                    if (isPressed) {
                        isPressed = false
                        isLoading = true
                    }
                }
                is ApiResult.Success ->{
                    Toast.makeText(context, "Success updated photo profile!", Toast.LENGTH_SHORT).show()
                    navController.navigateBack()
                    navController.navigateBack()
                }
                is ApiResult.Error ->{}
            }
        }
    }

    BasicScaffold(
        "",
        fullScreen = true
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            CameraContent(cameraController, lifecycleOwner)
            Image(
                painter = painterResource(R.drawable.ic_shutter),
                contentDescription = "Camera Shutter Button.",
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = NeoPadding.systemBarPadding)
                    .clickable(enabled = !isLoading) {
                        viewModel.capturePhoto(context, cameraController)
                        launchKey++
                        isPressed = true
                }
            )
        }
    }
    if (isLoading) Box(Modifier.fillMaxSize()) { LoadingIndicator() }
}
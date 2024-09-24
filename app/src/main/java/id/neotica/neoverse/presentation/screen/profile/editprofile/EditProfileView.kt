package id.neotica.neoverse.presentation.screen.profile.editprofile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import id.neotica.neoverse.presentation.screen.profile.ProfileViewModel
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.ImageCoil
import id.neotica.ui.components.NeoButton
import id.neotica.ui.navigation.RootScreen
import id.neotica.ui.navigation.Screen
import org.koin.androidx.compose.koinViewModel

@Composable
fun EditProfileView(
    navController: NavController, paddingValues: PaddingValues, modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = koinViewModel(),
    args: Screen.EditProfileScreen,
) {
    var selectedImages by remember {
        mutableStateOf<List<Uri?>>(emptyList())
    }

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            selectedImages = listOf(uri)
            if (uri != null) {
                viewModel.changeProfilePic(uri.toString())
                navController.navigate(RootScreen.AuthNav)
            }
        }
    )

    fun launchPhotoPicker() {
        singlePhotoPickerLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }
    BasicScaffold(
        topBarTitle = "Edit Profile",
        true,
        navController = navController,
        paddingValues = paddingValues,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val cameraPermissionResultLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
        ) {
            viewModel.onPermissionResult(
                permission = Manifest.permission.CAMERA,
                isGranted = it
            )
        }

        ImageCoil(url = args.profPic, context = LocalContext.current)

        NeoButton("ChangePhoto") {
            cameraPermissionResultLauncher.launch(Manifest.permission.CAMERA)
            launchPhotoPicker()
        }

    }
}

fun Activity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}
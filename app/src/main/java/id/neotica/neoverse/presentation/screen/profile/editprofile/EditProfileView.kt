package id.neotica.neoverse.presentation.screen.profile.editprofile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import id.neotica.neoverse.presentation.screen.profile.ProfileViewModel
import id.neotica.neoverse.utils.compressUri
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

    val context = LocalContext.current

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                val compressedImageUri = compressUri(uri, context)
                selectedImages = listOf(compressedImageUri)
                viewModel.changeProfilePic(compressedImageUri.toString())
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

        Column {
            Box(Modifier.padding(32.dp)) {
                ImageCoil(
                    url = args.profPic,
                    context = LocalContext.current,
                    modifier = Modifier.align(Alignment.Center),
                    round = true
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    NeoButton("Change your profile picture") {
                        launchPhotoPicker()
                    }

                    NeoButton("Take from camera", enabled = false) {
                        cameraPermissionResultLauncher.launch(Manifest.permission.CAMERA)
                    }
                }
            }
        }
    }
}

fun Activity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}
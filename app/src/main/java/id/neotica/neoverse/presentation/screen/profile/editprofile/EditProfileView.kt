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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
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
import id.neotica.ui.R
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.ImageCoil
import id.neotica.ui.components.MenuItem
import id.neotica.ui.navigation.RootScreen
import id.neotica.ui.navigation.Screen
import id.neotica.ui.theme.DarkBackground
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileView(
    navController: NavController, paddingValues: PaddingValues,
    viewModel: ProfileViewModel = koinViewModel(),
    args: Screen.EditProfileScreen,
) {
    var selectedImages by remember {
        mutableStateOf<List<Uri?>>(emptyList())
    }

    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                val compressedImageUri = compressUri(uri, context)
                selectedImages = listOf(compressedImageUri)
                viewModel.changeProfilePic(compressedImageUri.toString())
                navController.navigate(RootScreen.AuthNav) {
                    popUpTo(RootScreen.AuthNav) {
                        inclusive = true
                    }
                }
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

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                containerColor = DarkBackground
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    MenuItem("Use Gallery") {
                        launchPhotoPicker()
                    }
                    MenuItem("Use Camera") {
                        cameraPermissionResultLauncher.launch(Manifest.permission.CAMERA)
                        navController.navigate(Screen.CameraPreviewScreen)
                    }
                }

            }
        }

        Column {
            Box(Modifier.padding(32.dp)) {
                ImageCoil(
                    size = 100.dp,
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
                    MenuItem(
                        title = "Change Profile Picture",
                        leadingIcon = R.drawable.ic_edit
                    ) {
                        showBottomSheet = true
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
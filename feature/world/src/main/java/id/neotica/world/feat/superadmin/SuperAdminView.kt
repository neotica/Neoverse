package id.neotica.world.feat.superadmin

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.pm.PackageInfoCompat
import androidx.navigation.NavController
import id.neotica.domain.ApiResult
import id.neotica.droidcore.component.textfield.NumberField
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.NeoButton
import id.neotica.ui.navigation.RootScreen
import id.neotica.ui.navigation.Screen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SuperAdminView(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: SuperAdminViewModel = koinViewModel()
) {

    var currentAuras by remember { mutableStateOf<Long?>(null) }
    var versionCode by remember { mutableStateOf<Int?>(null) }

    val setAura = remember { mutableLongStateOf(0) }
    var auraInit by remember { mutableStateOf(false) }
    val minimumVersion = remember { mutableLongStateOf(0) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        Log.d("auraInit", auraInit.toString())

        viewModel.totalAuras.collect { result ->
            Log.d("auraInit", result.errorMessage. toString())
            when(result) {
                is ApiResult.Loading -> {
                    auraInit = false
                }
                is ApiResult.Success -> {
                    currentAuras = result.data
                    if (result.data == null) auraInit = true
                    result.data?.let {
                        if (it <= 0) {
                            Toast.makeText(context, "Aura is below", Toast.LENGTH_SHORT).show()
                            navController.navigateUp()
                        }
                    }
                }
                is ApiResult.Error -> {
                    if (result.data == null) auraInit = true
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        Log.d("auraInit", auraInit.toString())

        viewModel.versionCode.collect { result ->
            when(result) {
                is ApiResult.Loading -> {}
                is ApiResult.Success -> {
                    versionCode = result.data
                }
                is ApiResult.Error -> {}
            }
        }
    }

    LaunchedEffect(Unit) {
        if (!viewModel.isLoggedIn()) navController.navigate(Screen.LoginScreen) {
            popUpTo(RootScreen.HomeNav) {
                inclusive = true
            }
            launchSingleTop
        }
    }

    BasicScaffold(
        topBarTitle = "Super Admin",
        horizontalAlignment = Alignment.CenterHorizontally,
        navController = navController,
        enableActionBar = true
    ) {
        Text("Your Aura: ${currentAuras ?: ""}")
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        val version: Long = PackageInfoCompat.getLongVersionCode(packageInfo)

        Text("Current version: $version")
        Text("Minimum version : $versionCode")
        versionCode?.let {
            if (it >= version) Toast.makeText(context, "Update the app.", Toast.LENGTH_SHORT).show()
        }
        NumberField(setAura)
        NeoButton("Push ") {
            viewModel.sendAura(setAura.longValue)
        }
        Spacer(Modifier.padding(20.dp))
        NumberField(minimumVersion)
        NeoButton("Set minimum supported version ") {
            viewModel.postMinimumVersion(minimumVersion.longValue.toInt())
        }
        /*NeoButton("Drink Beer!") {
            viewModel.sendAuraPoints(-5)
        }*/
    }
}
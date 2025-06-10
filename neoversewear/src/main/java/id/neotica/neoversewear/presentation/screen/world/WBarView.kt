package id.neotica.neoversewear.presentation.screen.world

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.wear.compose.material.Text
import id.neotica.domain.ApiResult
import id.neotica.neoversewear.presentation.components.BasicWearScreen
import id.neotica.routes.RootScreen
import id.neotica.routes.Screen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WBarView(navController: NavController, viewModel: WBarViewModel = koinViewModel()) {
    var currentAuras by remember { mutableStateOf<Long?>(null) }
    var auraInit by remember { mutableStateOf(false) }
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
                }
                is ApiResult.Error -> {
                    if (result.data == null) auraInit = true
                }
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

    BasicWearScreen {
        item {
            Text("卐${currentAuras}")
        }
        item { WSlotMachine() }
        item { Text("Battery: ${batteryPercentage(context).substringBefore(".")}%") }
    }
}

private fun batteryPercentage(context: Context): String {
    val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
    val batteryStatus = context.registerReceiver(null, intentFilter)

    val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
    val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1

    val batteryPercent = level * 100 / scale.toFloat()

    return  "$batteryPercent"
}
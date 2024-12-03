package id.neotica.world.feat.world

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import id.neotica.domain.ApiResult
import id.neotica.droidcore.component.cards.Pocket
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.NeoButton
import id.neotica.ui.navigation.RootScreen
import id.neotica.ui.navigation.Screen
import id.neotica.ui.navigation.WorldScreen
import org.koin.androidx.compose.koinViewModel
import kotlin.random.Random

@Composable
fun WorldView(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: WorldViewModel = koinViewModel()
) {
    var currentAuras by remember { mutableStateOf<Long?>(null) }
    var auraInit by remember { mutableStateOf(false) }
    var onClick by remember { mutableStateOf(false) }
    val random = Random.nextInt(2000)
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(onClick) {

        viewModel.totalAuras.collect { result ->
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

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.fetchAuras()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
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
        topBarTitle = "World",
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (!auraInit) {
            Text("Your Aura: ${currentAuras ?: ""}")
            Spacer(Modifier.padding(4.dp))
            NeoButton("Leaderboards") {
                navController.navigate(WorldScreen.LeaderBoardsScreen)
            }
            Spacer(Modifier.padding(4.dp))
            NeoButton("Tavern") {
                navController.navigate(WorldScreen.GameScreen)
            }
            /*Spacer(Modifier.padding(4.dp))
            NeoButton("SuperAdmin") {
                navController.navigate(WorldScreen.SuperAdminScreen)
            }*/
            /*Spacer(Modifier.padding(4.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                NeoButton("To Market") {
                    navController.navigate(WorldScreen.MarketScreen)
                }
                NeoButton("To Inventory") {
                    navController.navigate(WorldScreen.InventoryScreen)
                }
            }
            Spacer(Modifier.padding(4.dp))
            NeoButton("To Merchant") {
                navController.navigate(WorldScreen.MerchantScreen)
            }*/
            Spacer(Modifier.padding(4.dp))
            NeoButton("Mining Field") {
                navController.navigate(WorldScreen.MiningScreen)
            }

        } else {
            Pocket(
                title = "Welcome to the Portal!"
            ) {
                Text("Welcome to the World Section! Ready to step into a realm of wonder? \uD83C\uDFF0 Venture through the mystical portal into Peking Town, a charming village brimming with secrets and adventure. \uD83D\uDDFA\uFE0F Your epic quest begins here. Tap the button below to unlock the portal and claim your aura!")
                Spacer(Modifier.padding(9.dp))
                Box(Modifier.padding(horizontal = 8.dp)) {
                    NeoButton(
                        text = "Get Aura!",
                        maxWidth = true,
                    ) {
                        Toast.makeText(context, "Congratulations, you've got $random aura!", Toast.LENGTH_SHORT).show()
                        viewModel.sendInitialAura(random.toLong())
                        onClick = !onClick
                        navController.navigate(WorldScreen.HomeScreen) {
                            popUpTo(RootScreen.WorldNav) {
                                inclusive = true
                            }
                        }
                    }
                }
            }
        }
    }
}
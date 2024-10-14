package id.neotica.world.feat.game

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import id.neotica.domain.ApiResult
import id.neotica.domain.model.Games
import id.neotica.droidcore.component.cards.deprecated.NeoCard
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.navigation.RootScreen
import id.neotica.ui.navigation.Screen
import id.neotica.world.feat.game.components.SlotMachine
import org.koin.androidx.compose.koinViewModel
import kotlin.random.Random

@Composable
fun GameView(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: GameViewModel = koinViewModel()
) {

    var currentAuras by remember { mutableStateOf<Games?>(null) }
    var auraInit by remember { mutableStateOf(false) }
    val random = Random.nextInt(2000)
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
                    if (result.data?.aura == null) auraInit = true
                }
                is ApiResult.Error -> {
                    if (result.data?.aura == null) auraInit = true
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

    BasicScaffold(
        topBarTitle = "Game",
        horizontalAlignment = Alignment.CenterHorizontally,
        navController = navController,
        enableActionBar = true
    ) {
        if (!auraInit) {
            Text("Your Aura:" + currentAuras?.aura.toString())

            Spacer(Modifier.padding(20.dp))
            Card {
                SlotMachine(
                    Modifier
                        .fillMaxWidth()
                        .padding(30.dp))
            }
        } else {
            NeoCard(
                title = "Receive your first time Aura!",
                desc = "hello",
                button = "Get Aura!"
            ) {
                Toast.makeText(context, "Congratulations, you've got $random aura!", Toast.LENGTH_SHORT).show()
                viewModel.sendInitialAura(random)
                navController.navigate(Screen.GameScreen) {
                    popUpTo(RootScreen.WorldNav) {
                        inclusive = true
                    }
                }
            }
        }
    }
}
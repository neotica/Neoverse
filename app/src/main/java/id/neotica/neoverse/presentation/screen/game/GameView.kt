package id.neotica.neoverse.presentation.screen.game

import android.widget.Toast
import androidx.compose.foundation.layout.Box
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
import id.neotica.droidcore.component.cards.deprecated.NeoCard
import id.neotica.neoverse.data.model.Games
import id.neotica.neoverse.data.remote.ApiResult
import id.neotica.neoverse.presentation.navigation.RootScreen
import id.neotica.neoverse.presentation.navigation.Screen
import id.neotica.neoverse.presentation.screen.game.components.SlotMachine
import id.neotica.neoverse.presentation.ui.components.BasicScaffold
import org.koin.androidx.compose.koinViewModel
import kotlin.random.Random

@Composable
fun HomeGameView(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: GameViewModel = koinViewModel()
) {

    var currentAuras by remember { mutableStateOf<Games?>(null) }
    var auraInit = remember { mutableStateOf(false) }
    val random = Random.nextInt(2000)
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.totalAuras.collect { result ->
            when(result) {
                is ApiResult.Loading -> {
                    auraInit.value = false
                }
                is ApiResult.Success -> {
                    currentAuras = result.data
                    if (result.data?.aura == null) auraInit.value = true
                }
                is ApiResult.Error -> {
                    auraInit.value = false
                }
            }
        }
    }

    BasicScaffold(
        topBarTitle = "Game",
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (!auraInit.value) {
            Text("Your Aura:" + currentAuras?.aura.toString())

            Spacer(Modifier.padding(20.dp))
            Card {
                SlotMachine(
                    Modifier
                        .fillMaxWidth()
                        .padding(30.dp))
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
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
                    popUpTo(RootScreen.GameNav) {
                        inclusive = true
                    }
                }
            }
        }
    }
}
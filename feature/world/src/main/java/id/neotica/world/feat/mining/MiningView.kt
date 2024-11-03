package id.neotica.world.feat.mining

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import id.neotica.domain.ApiResult
import id.neotica.domain.model.Games
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.ImageCoil
import id.neotica.ui.components.NeoButton
import id.neotica.world.utils.rockPicture
import org.koin.androidx.compose.koinViewModel

@Composable
fun MiningView(
    navController: NavController,
    paddingValues: PaddingValues,
    contentInsets: WindowInsets,
    viewModel: MiningViewModel = koinViewModel()
) {
    var coal by remember { mutableIntStateOf(0) }
    var currentAuras by remember { mutableStateOf<Games?>(null) }
    val onClick by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.totalAuras.collect { result ->
            when(result) {
                is ApiResult.Loading -> {}
                is ApiResult.Success -> {
                    currentAuras = result.data
                }
                is ApiResult.Error -> {}
            }
        }
    }

    BasicScaffold(
        topBarTitle = "Mining",
        horizontalAlignment = Alignment.CenterHorizontally,
        navController = navController,
        enableActionBar = true
    ) {
        Text(currentAuras?.aura.toString())
        Box(
            Modifier.clickable {
                coal += 1
            }
        ) {
            ImageCoil(
                url = rockPicture,
                size = 200.dp
            )
        }
        Text("Coal Mined: $coal")
        NeoButton("Exchange 100 Coals for 1 Aura") {
            if (coal >= 100) {
                coal -= 100
                viewModel.postAura(1)
            }
        }
    }
}
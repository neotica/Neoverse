package id.neotica.world.feat.mining

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import id.neotica.domain.ApiResult
import id.neotica.domain.model.World
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.ImagePermanentCache
import id.neotica.ui.components.NeoButton
import id.neotica.world.utils.rockPicture
import org.koin.androidx.compose.koinViewModel

@Composable
fun MiningView(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: MiningViewModel = koinViewModel()
) {
    var coal by remember { mutableIntStateOf(0) }
    var currentAuras by remember { mutableStateOf<World?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.totalAuras.collect { result ->
            when(result) {
                is ApiResult.Loading -> isLoading = true
                is ApiResult.Success -> {
                    isLoading = false
                    currentAuras = result.data
                }
                is ApiResult.Error -> isLoading = false
            }
        }
    }

    BasicScaffold(
        topBarTitle = "Mining ⛏\uFE0F",
        horizontalAlignment = Alignment.CenterHorizontally,
        navController = navController,
        enableActionBar = true
    ) {
        Text("Your Aura: ${currentAuras?.aura ?: ""}")

        ImagePermanentCache(
            context = LocalContext.current,
            fileName = "rockkkk",
            url = rockPicture,
        ) { coal += 1 }

        Text("Coal Mined: $coal")
        if (coal >= 100) {
            if (coal < 200) {
                NeoButton(
                    text = "Exchange 100 Coals for 1 Aura",
                ) {
                    if (coal >= 100 && !isLoading) {
                        coal -= 100
                        viewModel.postAura(1)
                    }
                }
            } else {
                val hundredsSum = coal / 100
                val coalSum = hundredsSum * 100

                NeoButton("Exchange $coalSum Coals for $hundredsSum Aura") {
                    if (coal >= 100 && !isLoading) {
                        coal -= coalSum
                        viewModel.postAura(hundredsSum)
                    }
                }
            }
        } else {
            Text("Tap the rock to mine the coal. The current rate for 1 aura is 100 coals.")
        }
    }
}
package id.neotica.world.feat.market.merchant

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import id.neotica.domain.ApiResult
import id.neotica.domain.model.Games
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.NeoButton
import id.neotica.ui.navigation.WorldScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun MerchantView(
    navController: NavController,
    paddingValues: PaddingValues,
    contentInsets: WindowInsets,
    viewModel: MerchantViewModel = koinViewModel()
) {
    var currentAuras by remember { mutableStateOf<Games?>(null) }

    LaunchedEffect(Unit) {
        viewModel.totalAuras.collect { result ->
            Log.d("auraCurrent", currentAuras.toString())

            when(result) {
                is ApiResult.Loading -> {}
                is ApiResult.Success -> {
                    currentAuras = result.data
                }
                is ApiResult.Error -> {}
            }
        }
    }

    BasicScaffold("Merchant", contentInset = contentInsets) {
        NeoButton("To Hero Maker") {
            navController.navigate(WorldScreen.HeroMakerScreen)
        }
    }
}
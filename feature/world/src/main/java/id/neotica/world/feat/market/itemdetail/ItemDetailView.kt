package id.neotica.world.feat.market.itemdetail

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import id.neotica.domain.ApiResult
import id.neotica.domain.model.Games
import id.neotica.domain.model.market.ItemModel
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.ImageCoil
import id.neotica.ui.components.LoadingIndicatorBool
import org.koin.androidx.compose.koinViewModel

@Composable
fun ItemDetailView(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: ItemDetailViewModel = koinViewModel()
) {
    var currentAuras by remember { mutableStateOf<Games?>(null) }
    var onClick by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    var marketItems by remember { mutableStateOf<List<ItemModel>>(emptyList()) }
    var itemModel by remember { mutableStateOf<ItemModel?>(null) }

    LaunchedEffect(onClick) {
        viewModel.totalAuras.collect { result ->

            when(result) {
                is ApiResult.Loading -> {
                    loading = true
                }
                is ApiResult.Success -> {
                    loading = false
                    currentAuras = result.data
                }
                is ApiResult.Error -> loading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.itemModel.collect { result ->

            when(result) {
                is ApiResult.Loading -> {}
                is ApiResult.Success -> {
                    itemModel = result.data
                }
                is ApiResult.Error -> {}
            }
        }
    }

    BasicScaffold(
        topBarTitle = "Market",
        horizontalAlignment = Alignment.CenterHorizontally,
        navController = navController,
        enableActionBar = true
    ) {
        Text("Your Aura: ${currentAuras?.aura ?: ""}")
        LoadingIndicatorBool(loading) {
            itemModel?.let {
                ImageCoil(it.imageUrl)
                Text("Name: ${it.name}")
                Text("Owner: ${it.ownerName}")
                Text("Power: ${it.power}")
                Text("Price: 卐${it.price}")
                Text("Id: ${it.id}")
            }
        }
    }
}
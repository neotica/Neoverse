package id.neotica.world.feat.market

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import id.neotica.domain.ApiResult
import id.neotica.domain.model.Games
import id.neotica.domain.model.market.ItemModel
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.ImageCoil
import id.neotica.ui.components.NeoButton
import id.neotica.ui.navigation.Screen
import org.koin.androidx.compose.koinViewModel

@Composable
fun MarketView(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: MarketViewModel = koinViewModel()
) {
    var currentAuras by remember { mutableStateOf<Games?>(null) }
    var onClick by remember { mutableStateOf(false) }
    var marketItems by remember { mutableStateOf<List<ItemModel>>(emptyList()) }

    LaunchedEffect(onClick) {
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

    LaunchedEffect(Unit) {
        viewModel.itemModel.collect { result ->

            when(result) {
                is ApiResult.Loading -> {}
                is ApiResult.Success -> {
                    Log.d("marketdata", result.data.toString())
                    result.data?.let {
                        marketItems = it
                    }

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
        Text(currentAuras.toString())
        NeoButton("Add Item") {
            viewModel.addItem()
        }
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Hot items!")
            Text("More items.. >>>", Modifier.clickable {  })
        }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(marketItems) { item ->
                Card(
                    Modifier.clickable { navController.navigate(Screen.ItemDetailScreen(id = item.id)) }
                ) {
                    Column {
                        ImageCoil(item.imageUrl)
                        Text("Name: " + item.name)
                        Text("Owner: " + item.ownerName)
                        Text("Power: " + item.power)
                        Text("卐${item.price}")
                    }
                }

            }
        }
    }
}
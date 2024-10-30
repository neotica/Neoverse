package id.neotica.world.feat.inventory

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import id.neotica.domain.ApiResult
import id.neotica.domain.model.Games
import id.neotica.domain.model.market.Hero
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.ImageCoil
import id.neotica.ui.navigation.WorldScreen
import id.neotica.ui.state.ScreenState
import id.neotica.ui.theme.DarkPrimaryTransparent2
import id.neotica.ui.theme.FontSize
import org.koin.androidx.compose.koinViewModel

@Composable
fun InventoryView(
    navController: NavController,
    paddingValues: PaddingValues,
    contentInsets: WindowInsets,
    viewModel: InventoryViewModel = koinViewModel()
) {
    var currentAuras by remember { mutableStateOf<Games?>(null) }
    val onClick by remember { mutableStateOf(false) }
    var marketItems by remember { mutableStateOf<List<Hero>>(emptyList()) }
    val lazyState = rememberLazyListState()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(onClick) {
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

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.fetchItem()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    BasicScaffold(
        topBarTitle = "Inventory",
        horizontalAlignment = Alignment.CenterHorizontally,
        navController = navController,
        enableActionBar = true,
        contentInset = contentInsets
    ) {
        LazyColumn {
            item {
                Text("Your Aura: ${currentAuras?.aura ?: ""}")

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    state = lazyState
                ) {
                    items(marketItems.take(8)) { item ->
                        Card(
                            Modifier
                                .clickable { navController.navigate(WorldScreen.ItemDetailScreen(id = item.id, ScreenState.UserInventory.toString())) }
                                .wrapContentHeight()
                                .padding(2.dp),
                            colors = CardDefaults.cardColors().copy(
                                containerColor = DarkPrimaryTransparent2,
                                contentColor = Color.White
                            )
                        ) {
                            Column {
                                Box(
                                    Modifier.size(124.dp)
                                ) {
                                    ImageCoil(
                                        item.imageUrl,
                                        scaleCustom = true,
                                        contentScale = ContentScale.Crop,
                                        fillMaxWidth = true,
                                        customModifier = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                Column(Modifier.padding(4.dp)) {
                                    Text(item.name)
                                    Text(
                                        "⚔${item.power}",
                                        fontSize = FontSize.Small
                                    )
                                    Text(
                                        "卐${item.price}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = FontSize.Normal
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
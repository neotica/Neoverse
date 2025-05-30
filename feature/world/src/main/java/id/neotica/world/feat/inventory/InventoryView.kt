package id.neotica.world.feat.inventory

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import id.neotica.domain.ApiResult
import id.neotica.domain.model.World
import id.neotica.domain.model.market.Hero
import id.neotica.domain.model.market.Item
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.image.ImageCoil
import id.neotica.ui.navigation.WorldScreen
import id.neotica.ui.state.ScreenState
import id.neotica.ui.theme.DarkPrimaryTransparent2
import id.neotica.ui.theme.FontSize
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun InventoryView(
    navController: NavController,
    viewModel: InventoryViewModel = koinViewModel()
) {
    var currentAuras by remember { mutableStateOf<World?>(null) }
    val onClick by remember { mutableStateOf(false) }
    var marketHeroes by remember { mutableStateOf<List<Hero>>(emptyList()) }
    var items by remember { mutableStateOf<List<Item>>(emptyList()) }
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
        viewModel.hero.collect { result ->

            when(result) {
                is ApiResult.Loading -> {}
                is ApiResult.Success -> {
                    result.data?.let {
                        marketHeroes = it
                    }
                }
                is ApiResult.Error -> {}
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.items.collect { result ->

            when(result) {
                is ApiResult.Loading -> {}
                is ApiResult.Success -> {
                    result.data?.let {
                        items = it
                    }
                }
                is ApiResult.Error -> {}
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.fetchHeroes()
                viewModel.fetchItems()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    BasicScaffold(
        topBarTitle = "Inventory",
        navController = navController,
        enableActionBar = true,
    ) {
        LazyColumn {
            item {
                Text("Your Aura: ${currentAuras?.aura ?: ""}")
                Text("Hero")
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    state = lazyState
                ) {
                    items(marketHeroes.take(8)) { heroItem ->
                        Card(
                            Modifier
                                .clickable {
                                    navController.navigate(
                                        WorldScreen.ItemDetailScreen(
                                            id = heroItem.id,
                                            ScreenState.UserInventory.toString(),
                                        )
                                    )
                                }
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
                                        heroItem.imageUrl,
                                        scaleCustom = true,
                                        contentScale = ContentScale.Crop,
                                        fillMaxWidth = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                Column(Modifier.padding(4.dp)) {
                                    Text(heroItem.name)
                                    Text(
                                        "⚔${heroItem.power}",
                                        fontSize = FontSize.Small
                                    )
                                    Text(
                                        "卐${heroItem.price}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = FontSize.Normal
                                    )
                                }
                            }
                        }
                    }
                }
                Text("Items")
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    state = lazyState
                ) {
                    items(items.take(8)) { item ->
                        Card(
                            Modifier
                                .clickable {
                                    navController.navigate(
                                        WorldScreen.ItemDetailScreen(
                                            id = item.id,
                                            ScreenState.InventoryItem.toString(),
                                        )
                                    )
                                }
                                .wrapContentHeight()
                                .padding(2.dp),
                            colors = CardDefaults.cardColors().copy(
                                containerColor = DarkPrimaryTransparent2,
                                contentColor = Color.White
                            )
                        ) {
                            Column(Modifier.width(144.dp)) {
                                Box(
                                    Modifier.height(124.dp)
                                ) {
                                    ImageCoil(
                                        item.imageUrl,
                                        scaleCustom = true,
                                        contentScale = ContentScale.Crop,
                                        fillMaxWidth = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                Column(Modifier.padding(4.dp)) {
                                    Text(
                                        item.name,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        "⚔${item.power}",
                                        fontSize = FontSize.Small,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    if (item.price.coal.toInt() != 0) {
                                        Text(
                                            "\uD83E\uDEA8${item.price.coal}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = FontSize.Normal,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    if (item.price.aura.toInt() != 0) {
                                        Text(
                                            "卐${item.price.aura}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = FontSize.Normal,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
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
}
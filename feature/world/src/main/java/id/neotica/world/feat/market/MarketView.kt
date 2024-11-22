package id.neotica.world.feat.market

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.CarouselDefaults
import androidx.compose.material3.carousel.CarouselState
import androidx.compose.material3.carousel.HorizontalUncontainedCarousel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import id.neotica.domain.model.Games
import id.neotica.domain.model.market.Hero
import id.neotica.domain.model.market.Item
import id.neotica.droidcore.component.carousel.CarouselCard
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.ImageCoil
import id.neotica.ui.components.scroll.scrollUpValue
import id.neotica.ui.navigation.WorldScreen
import id.neotica.ui.state.ScreenState
import id.neotica.ui.theme.DarkPrimaryCard
import id.neotica.ui.theme.DarkPrimaryTransparent2
import id.neotica.ui.theme.DarkTransparent
import id.neotica.ui.theme.FontSize
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketView(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: MarketViewModel = koinViewModel()
) {
    var currentAuras by remember { mutableStateOf<Games?>(null) }
    val onClick by remember { mutableStateOf(false) }
    var heroes by remember { mutableStateOf<List<Hero>>(emptyList()) }
    var items by remember { mutableStateOf<List<Item>>(emptyList()) }
    val carouselState = CarouselState(itemCount = { heroes.count() })
    val lifecycleOwner = LocalLifecycleOwner.current

    /**This is the state to make the top app bar disappear/appears on lazyList scroll**/
    val listState = rememberLazyListState()

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
                        heroes = it
                    }

                }
                is ApiResult.Error -> {}
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.item.collect { result ->
            Log.d("market item", result.toString())

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
        topBarTitle = "Market",
        horizontalAlignment = Alignment.CenterHorizontally,
        navController = navController,
        enableActionBar = true,
        topBarVisibility = scrollUpValue(listState),
    ) {
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(bottom = paddingValues.calculateBottomPadding())
        ) {
            item {
                val currentPage = rememberSaveable { mutableIntStateOf(0) }
                CarouselCard(
                    list = heroes.take(5),
                    /*.filter { it.power > 1000 }.take(5).sortedByDescending { it.power },*/
                    currentPageState = currentPage
                ) { item ->
                    val state = if (viewModel.currentUserId() == item.owner.id) ScreenState.User.toString() else ScreenState.Global.toString()
                    Card(
                        Modifier.clickable {
                            navController.navigate(WorldScreen.ItemDetailScreen(id = item.id, state))
                        }
                    ) {
                        Box {
                            ImageCoil(
                                item.imageUrl,
                                scaleCustom = true,
                                contentScale = ContentScale.FillWidth,
                                fillMaxWidth = true,
                                size = 200.dp,
                                customModifier = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .background(DarkTransparent, shape = RoundedCornerShape(16.dp))
                                    .align(Alignment.BottomStart),
                            ) {
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    Text(
                                        text = "" + item.name,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.padding(4.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Hot Heroes!",
                        fontWeight = FontWeight.Bold,
                        fontSize = FontSize.Normal
                    )
                    Text(
                        text = "More heroes.. >>>",
                        modifier = Modifier.clickable {  },
                        fontWeight = FontWeight.Bold,
                        fontSize = FontSize.Normal
                    )
                }
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    state = rememberLazyListState()
                ) {
                    items(heroes.take(8)) { item ->
                        val state = if (viewModel.currentUserId() == item.owner.id) ScreenState.User.toString() else ScreenState.Global.toString()
                        Card(
                            Modifier
                                .clickable {
                                    navController.navigate(
                                        WorldScreen.ItemDetailScreen(
                                            id = item.id,
                                            state
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
                                        customModifier = true,
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
                                    Text(
                                        "卐${item.price}",
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
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Items!",
                        fontWeight = FontWeight.Bold,
                        fontSize = FontSize.Normal,
                    )
                    Text(
                        text = "More Items.. >>>",
                        modifier = Modifier.clickable {  },
                        fontWeight = FontWeight.Bold,
                        fontSize = FontSize.Normal
                    )
                }
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    state = rememberLazyListState()
                ) {
                    items(items.take(8)) { item ->
                        val state = ScreenState.GlobalItem.toString()
                        Card(
                            Modifier
                                .clickable {
                                    navController.navigate(
                                        WorldScreen.ItemDetailScreen(
                                            id = item.id,
                                            state
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
                                        customModifier = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                Column(
                                    Modifier
                                        .padding(4.dp)
                                ) {
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
                Spacer(Modifier.padding(12.dp))
                HorizontalUncontainedCarousel(
                    state = carouselState,
                    itemWidth = 250.dp,
                    itemSpacing = 12.dp,
                    flingBehavior = CarouselDefaults.singleAdvanceFlingBehavior(carouselState),
                ) { index ->
                    val value = heroes[index]
                    val state = if (viewModel.currentUserId() == value.owner.id) ScreenState.User.toString() else ScreenState.Global.toString()
                    Card(
                        Modifier.clickable {
                            navController.navigate(WorldScreen.ItemDetailScreen(id = value.id, state))
                        },
                        colors = CardDefaults.cardColors().copy(
                            containerColor = DarkPrimaryCard,
                            contentColor = Color.White)
                    ) {
                        Column {
                            ImageCoil(value.imageUrl)
                            Text("Name: " + value.name)
                            Text("Owner: " + value.owner.name)
                            Text("Power: " + value.power)
                            Text("卐${value.price}")
                        }
                    }
                }
                Spacer(Modifier.padding(4.dp))
            }
        }
    }
}
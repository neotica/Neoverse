package id.neotica.world.feat.market.itemdetail

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import id.neotica.domain.ApiResult
import id.neotica.domain.model.Games
import id.neotica.domain.model.market.Hero
import id.neotica.domain.model.market.Item
import id.neotica.droidcore.component.alert.AlertDialogCustomDesc
import id.neotica.droidcore.component.textfield.NumberField
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.ImageCoil
import id.neotica.ui.components.LoadingIndicator
import id.neotica.ui.components.NeoButton
import id.neotica.ui.state.ScreenState
import id.neotica.ui.theme.FontSize
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
    var hero by remember { mutableStateOf<Hero?>(null) }
    var item by remember { mutableStateOf<Item?>(null) }
    val context = LocalContext.current

    var openDialog by remember { mutableStateOf(false) }
    val auraCustom = remember { mutableLongStateOf(0) }

    if (openDialog) {
        AlertDialogCustomDesc(
            openDialog = { openDialog = false },
            title = "Set the pawn's aura value.",
            desc = {
                NumberField(
                    auraCustom,
                    placeHolder = hero?.price.toString()
                )
            },
            confirmButton = {
                NeoButton("Okay") {
                    if (hero != null) {
                        viewModel.pawn(auraCustom.longValue)
                        navController.navigateUp()
                        navController.navigateUp()
                    } else Toast.makeText(
                        context,
                        "there is no auras!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            backButton = "Cancel"
        )
    }

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

    LaunchedEffect(onClick) {
        hero?.owner?.let { viewModel.fetchSellerAuras(it.id) }
        viewModel.hero.collect { result ->
            when(result) {
                is ApiResult.Loading -> {}
                is ApiResult.Success -> {
                    hero = result.data
                }
                is ApiResult.Error -> {}
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.item.collect { result ->
            when(result) {
                is ApiResult.Loading -> {}
                is ApiResult.Success -> {
                    item = result.data
                }
                is ApiResult.Error -> {}
            }
        }
    }

    LaunchedEffect(onClick) {
        viewModel.buyHeroState.collect { result ->
            when(result) {
                is ApiResult.Loading -> {
                    loading = true
                }
                is ApiResult.Success -> {
                    navController.navigateUp()
                }
                is ApiResult.Error -> {}
            }
        }
    }

    val scaffoldTitle = when(viewModel.screenState()) {
        ScreenState.UserInventory.toString(), ScreenState.InventoryItem.toString() -> "Inventory"
        else -> "Market"
    }

    BasicScaffold(
        topBarTitle = scaffoldTitle,
        horizontalAlignment = Alignment.CenterHorizontally,
        navController = navController,
        enableActionBar = true
    ) {
        Box(
            Modifier.fillMaxSize()
        ) {
            if (loading) LoadingIndicator()
            hero?.let {
                LazyColumn(
                    Modifier
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(bottom = 30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    item {
                        Box {
                            Column {
                                Box(
                                    modifier = Modifier.height(200.dp)
                                ) {
                                    ImageCoil(
                                        it.imageUrl,
                                        scaleCustom = true,
                                        contentScale = ContentScale.FillWidth,
                                        fillMaxWidth = true,
                                        customModifier = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                Spacer(Modifier.padding(4.dp))
                                Text(
                                    it.name.capitalize(Locale.current),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = FontSize.Big
                                )
                                Spacer(Modifier.padding(4.dp))
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    Text("⚔ ${it.power}")
                                    Text("卐${it.price}")
                                }
                                Spacer(Modifier.padding(8.dp))

                                if (it.background?.origin != "") {
                                    Spacer(Modifier.padding(8.dp))
                                    Text("Origin: ${it.background?.origin}")
                                }
                                if (it.background?.story != "") Text("Story: ${it.background?.story}")
                                if (it.background?.pantheon != "")
                                    Text("Pantheon: ${it.background?.pantheon}")
                            }
                            Column {
                                Text(
                                    modifier = Modifier.weight(1f),
                                    text =  "Owned by ${it.owner.name}"
                                )
                            }
                        }
                    }
                }
            }
            item?.let {
                LazyColumn(
                    Modifier
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(bottom = 30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    item {
                        Text(
                            it.name.capitalize(Locale.current),
                            fontWeight = FontWeight.Bold,
                            fontSize = FontSize.Medium
                        )
                        Box(
                            modifier = Modifier.size(200.dp)
                        ) {
                            ImageCoil(
                                it.imageUrl,
                                scaleCustom = true,
                                contentScale = ContentScale.Inside,
                                fillMaxWidth = true,
                                customModifier = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        Spacer(Modifier.padding(4.dp))
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Text("⚔ ${it.power}")
                            Text("卐${it.price.aura}")
                            Text("\uD83E\uDEA8${it.price.coal}")
                        }
                        Text("Stock: ${it.stock}")
                        Spacer(Modifier.padding(8.dp))
                    }
                }
            }
            LazyColumn(
                modifier = Modifier.align(Alignment.BottomCenter),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                item {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        /* NeoButton(
                             "Chat",
                             modifier = Modifier
                                 .weight(1f)
                                 .fillMaxWidth(),
                             enabled = false
                         )
                         Spacer(Modifier.padding(4.dp))*/
                        when (viewModel.screenState()) {
                            ScreenState.GlobalItem.toString() -> {
                                var onBuy by remember { mutableStateOf(false) }
                                LaunchedEffect(onBuy) {
                                    if (onBuy) {
                                        onBuy = false
                                        if (item != null) viewModel.buyItem(item!!)
                                    }
                                }
                                NeoButton(
                                    "Buy",
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth(),
                                    enabled = item != null
                                ) {
                                    if (item != null) {
                                        onBuy = true
                                        navController.navigateUp()
                                    } else Toast.makeText(
                                        context,
                                        "there is no auras!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            ScreenState.Global.toString() -> {
                                NeoButton(
                                    "Buy",
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth(),
                                    enabled = hero?.price != null
                                ) {
                                    viewModel.buy(hero!!)
                                    onClick = true
                                }
                            }
                            ScreenState.UserInventory.toString() -> {
                                NeoButton(
                                    "Pawn",
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth(),
                                    enabled = hero?.price != null
                                ) {
                                    openDialog = true
                                }
                            }
                            ScreenState.User.toString() -> {
                                NeoButton(
                                    "Store",
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth(),
                                    enabled = hero?.price != null
                                ) {
                                    if (hero != null) {
                                        viewModel.store(hero?.price!!)
                                        viewModel.getHeroDetail()
                                        onClick = true

                                    } else {

                                        Toast.makeText(
                                            context,
                                            "there is no auras!",
                                            Toast.LENGTH_SHORT
                                        ).show()
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
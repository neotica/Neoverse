package id.neotica.world.feat.market.merchant

import android.util.Log
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
import androidx.navigation.NavController
import id.neotica.domain.ApiResult
import id.neotica.domain.model.Games
import id.neotica.domain.model.market.Hero
import id.neotica.domain.model.market.HeroBackground
import id.neotica.droidcore.component.textfield.NeoTextField
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.LoadingIndicatorBool
import id.neotica.ui.components.NeoButton
import id.neotica.ui.components.NumberField
import org.koin.androidx.compose.koinViewModel

@Composable
fun MerchantView(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: MerchantViewModel = koinViewModel()
) {
    var currentAuras by remember { mutableStateOf<Games?>(null) }
    val onClick by remember { mutableStateOf(false) }

    var marketItems by remember { mutableStateOf<List<Hero>>(emptyList()) }
    val name = remember { mutableStateOf("") }
    val power = remember { mutableIntStateOf(0) }
    val price = remember { mutableIntStateOf(0) }
    val story = remember { mutableStateOf("") }
    val origin = remember { mutableStateOf("") }
    val pantheon = remember { mutableStateOf("") }

    var buttonDisabled by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var sendResult by remember { mutableStateOf("") }

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
        viewModel.merchant.collect { result ->
            when(result) {
                is ApiResult.Loading -> {
                    isLoading = true
                }
                is ApiResult.Success -> {
                    Log.d("marketdata", result.data.toString())
                    isLoading = false
                    result.data?.let {
                        sendResult = it
                        navController.navigateUp()
                    }
                }
                is ApiResult.Error -> {
                    isLoading = false
                }
            }
        }
    }

    BasicScaffold(
        topBarTitle = "Market",
        horizontalAlignment = Alignment.CenterHorizontally,
        navController = navController,
        enableActionBar = true,
    ) {
        LoadingIndicatorBool(!isLoading) {
            Text("Your Aura: ${currentAuras?.aura ?: ""}")
            NeoTextField(name, label = "Name")
            NumberField(power, label = "Power")
            NumberField(price, label = "Price")
            NeoTextField(story, label = "Hero Story")
            NeoTextField(origin, label = "Hero Origin")
            NeoTextField(pantheon, label = "Hero Pantheon")
            val heroInput = Hero(
                name = name.value,
                power = power.intValue,
                price = price.intValue,
                background = HeroBackground(
                    story = story.value,
                    origin = origin.value,
                    pantheon = pantheon.value,
                ),
                imageUrl = ""
            )
            NeoButton("Add Item") {
                viewModel.addItem(heroInput)
            }
        }

    }
}
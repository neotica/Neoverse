package id.neotica.world.feat.mining

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import id.neotica.domain.ApiResult
import id.neotica.droidcore.component.alert.NeoAlert
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.image.ImagePermanentCache
import id.neotica.ui.components.NeoButton
import id.neotica.ui.navigation.navigateBack
import id.neotica.world.utils.rockPicture
import org.koin.androidx.compose.koinViewModel

@Composable
fun MiningView(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: MiningViewModel = koinViewModel()
) {
    var coal by remember { mutableIntStateOf(0) }
    var currentAuras by remember { mutableStateOf<Long?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val openDialog = remember { mutableStateOf(false) }

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

    BackHandler {
        openDialog.value = true
    }

    if (openDialog.value) {
        NeoAlert(
            openDialog = openDialog,
            title = "Exit Mining Cave?",
            desc = "You are leaving the Cave. Legend has it that once travelers depart, dwarves emerge from their hidden dwellings to claim all the resources you’ve worked so hard to mine. When you return, the coal you left behind will be gone.",
            backButton = "Cancel",
            hasIcon = true,
            confirmButton = {
                NeoButton("Okay!") { navController.navigateBack() }
            }
        )
    }

    BasicScaffold(
        topBarTitle = "Mining ⛏\uFE0F",
        horizontalAlignment = Alignment.CenterHorizontally,
        navController = navController,
        customActionBar = { openDialog.value = true },
        enableActionBar = true
    ) {
        LazyColumn(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text("Your Aura: ${currentAuras ?: ""}")

                ImagePermanentCache(
                    context = LocalContext.current,
                    fileName = "obj_rock",
                    url = rockPicture,
                    size = 200.dp
                ) { coal += 1 }

                Text("Coal Mined: $coal")
                if (coal >= 100) {
                    val hundredsSum = coal / 100
                    val coalSum = hundredsSum * 100

                    NeoButton("Exchange $coalSum Coals for $hundredsSum Aura") {
                        if (coal >= 100 && !isLoading) {
                            coal -= coalSum
                            viewModel.postAura(hundredsSum)
                        }
                    }
                } else {
                    Text("Tap the rock to mine the coal. The current rate for 1 aura is 100 coals.")
                }
            }
        }
    }
}
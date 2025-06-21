package id.neotica.world.feat.world

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.pm.PackageInfoCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import id.neotica.domain.ApiResult
import id.neotica.droidcore.component.cards.Pocket
import id.neotica.droidcore.component.carousel.CarouselCard
import id.neotica.routes.RootScreen
import id.neotica.routes.Screen
import id.neotica.routes.WorldScreen
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.NeoButton
import id.neotica.ui.components.world.WorldCard
import id.neotica.world.feat.card.LibraryCard
import id.neotica.world.feat.card.RavenCard
import id.neotica.world.utils.bgLeaderBoards
import id.neotica.world.utils.bgMining
import id.neotica.world.utils.bgTavern
import id.neotica.world.utils.worldIntro2
import org.koin.compose.viewmodel.koinViewModel
import kotlin.random.Random

@Composable
fun WorldView(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: WorldViewModel = koinViewModel()
) {
    var currentAuras by remember { mutableStateOf<Long?>(null) }
    var auraInit by remember { mutableStateOf(false) }
    var onClick by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var versionCode by remember { mutableStateOf<Int?>(null) }

    val random = Random.nextInt(2000)
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val test = ""

    val worldCardItems = listOf(
        WorldCardItem(
            title = "Tavern",
            fileName = "bg_tavern$test",
            url = bgTavern,
            navigateTo = { navController.navigate(WorldScreen.TavernScreen) }
        ),
        WorldCardItem(
            title = "Mining Cave",
            fileName = "bg_mining$test",
            url = bgMining,
            navigateTo = { navController.navigate(WorldScreen.MiningScreen) }
        ),
        WorldCardItem(
            title = "Leaderboards",
            fileName = "bg_leaderboards$test",
            url = bgLeaderBoards,
            navigateTo = { navController.navigate(WorldScreen.LeaderBoardsScreen) }
        )
    )

    LaunchedEffect(Unit) {
        viewModel.versionCode.collect { result ->
            when(result) {
                is ApiResult.Loading -> {
                    isLoading = true
                }
                is ApiResult.Success -> {
                    isLoading = false
                    versionCode = result.data
                    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                    val version: Long = PackageInfoCompat.getLongVersionCode(packageInfo)
                    versionCode?.let {
                        if (it > version) {
                            navController.navigate(Screen.UpdateAppScreen) {
                                popUpTo(RootScreen.HomeNav) {
                                    inclusive = true
                                }
                                launchSingleTop
                            }
                        }
                    }
                }
                is ApiResult.Error -> {
                    isLoading = false
                }
            }
        }
    }

    LaunchedEffect(onClick) {
        viewModel.totalAuras.collect { result ->
            when(result) {
                is ApiResult.Loading -> {
                    auraInit = false
                }
                is ApiResult.Success -> {
                    currentAuras = result.data
                    if (result.data == null) auraInit = true
                }
                is ApiResult.Error -> {
                    if (result.data == null) auraInit = true
                }
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.fetchAuras()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(Unit) {
        if (!viewModel.isLoggedIn()) navController.navigate(Screen.LoginScreen) {
            popUpTo(RootScreen.HomeNav) {
                inclusive = true
            }
            launchSingleTop
        }
    }

    val currentPage = rememberSaveable { mutableIntStateOf(0) }

    BasicScaffold(
        topBarTitle = "World",
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (!auraInit) {
            Text("Your Aura: ${currentAuras ?: ""}")
            Spacer(Modifier.padding(4.dp))
            CarouselCard(
                list = worldCardItems,
                currentPageState = currentPage
            ) { item ->
                WorldCard(
                    title = item.title,
                    fileName = item.fileName,
                    url = item.url,
                    onClick = item.navigateTo
                )
            }
            RavenCard(navController)
            LibraryCard(navController)
        } else {
            Pocket(
                title = "Welcome to the Portal!"
            ) {
                Text(worldIntro2)
                Spacer(Modifier.padding(9.dp))
                Box(Modifier.padding(horizontal = 8.dp)) {
                    NeoButton(
                        text = "Get Aura!",
                        maxWidth = true,
                    ) {
                        Toast.makeText(context, "Congratulations, you've got $random aura!", Toast.LENGTH_SHORT).show()
                        viewModel.sendInitialAura(random.toLong())
                        onClick = !onClick
                        navController.navigate(WorldScreen.HomeScreen) {
                            popUpTo(RootScreen.WorldNav) {
                                inclusive = true
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class WorldCardItem(
    val title: String,
    val fileName: String,
    val url: String,
    val navigateTo: () -> Unit
)
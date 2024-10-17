package id.neotica.world.feat.market

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import id.neotica.ui.components.BasicScaffold
import org.koin.androidx.compose.koinViewModel

@Composable
fun MarketView(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: MarketViewModel = koinViewModel()
) {


    BasicScaffold(
        topBarTitle = "Market",
        horizontalAlignment = Alignment.CenterHorizontally,
        navController = navController,
        enableActionBar = true
    ) {
    }
}
package id.neotica.world.feat.mining

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import id.neotica.ui.components.BasicScaffold
import org.koin.androidx.compose.koinViewModel

@Composable
fun MiningView(
    navController: NavController,
    paddingValues: PaddingValues,
    contentInsets: WindowInsets,
    viewModel: MiningViewModel = koinViewModel()
) {

    BasicScaffold(
        topBarTitle = "Mining",
        horizontalAlignment = Alignment.CenterHorizontally,
        navController = navController,
        enableActionBar = true
    ) {
        Text(viewModel.getAura().toString())
    }
}
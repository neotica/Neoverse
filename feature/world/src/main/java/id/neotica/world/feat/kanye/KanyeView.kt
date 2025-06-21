package id.neotica.world.feat.kanye

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import id.neotica.domain.ApiResult
import id.neotica.domain.model.kanye.KanyeQuotes
import id.neotica.ui.components.BasicScaffold
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun KanyeView(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: KanyeViewModel = koinViewModel()
) {

    var quote by remember { mutableStateOf<KanyeQuotes?>(null) }

    LaunchedEffect(Unit) {
        viewModel.fetchQuote()
        viewModel.quote.collect { result ->
            Log.d("kanye", result.toString())
            when(result) {
                is ApiResult.Loading -> {
                    //dsfds
                }
                is ApiResult.Success -> {
                    quote = result.data
                }
                is ApiResult.Error -> {}
            }


        }
    }

    BasicScaffold(
        topBarTitle = "Kanye Quotes",
        horizontalAlignment = Alignment.CenterHorizontally,
        navController = navController,
        enableActionBar = true
    ) {
        Text(quote?.quote.toString())
    }
}
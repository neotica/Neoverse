package id.neotica.world.feat.raven

import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import id.neotica.domain.ApiResult
import id.neotica.domain.model.raven.Raven
import id.neotica.droidcore.component.textfield.NeoTextField
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.NeoButton
import org.koin.androidx.compose.koinViewModel

@Composable
fun RavenView(
    navController: NavController,
    viewModel: RavenViewModel = koinViewModel()
) {
    val words = remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        viewModel.words.collect { result ->
            Log.d("ravenview", result.toString())
            when(result) {
                is ApiResult.Loading -> {}
                is ApiResult.Success -> {
                    if (result.data != null) {
                        words.value = result.data!!.random()
                    }
                    Log.d("ravenview", words.toString())
                }
                is ApiResult.Error -> {}
            }
        }
    }

    BasicScaffold("Raven") {
        val word = remember { mutableStateOf("") }
        val value = remember { mutableStateOf("") }

        NeoTextField(word, label = "word")
        NeoTextField(value, label = "value")
        NeoButton("Post Words") {
            val raven = if (word.value.isNotEmpty()) {
                Raven(
                    word = word.value,
                    value = value.value
                )
            } else null
            viewModel.postWord(raven)
        }
        NeoButton("Get Words") {
            viewModel.getWords(word = word.value)
        }
        Text(words.value)
    }
}
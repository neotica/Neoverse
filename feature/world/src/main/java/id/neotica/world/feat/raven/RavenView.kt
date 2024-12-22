package id.neotica.world.feat.raven

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
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
    BasicScaffold("Raven") {
        val word = remember { mutableStateOf("") }
        val value = remember { mutableStateOf("") }

        NeoTextField(word)
        NeoTextField(value)
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
            viewModel.getWords()
        }
    }
}
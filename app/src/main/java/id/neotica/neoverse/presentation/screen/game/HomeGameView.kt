package id.neotica.neoverse.presentation.screen.game

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import id.neotica.neoverse.presentation.ui.components.BasicScreen
import kotlin.random.Random

@Composable
fun HomeGameView(navController: NavController, paddingValues: PaddingValues) {
    BasicScreen(paddingValues = paddingValues) {
        Text("home game")
        val randomStrings = remember { mutableStateListOf(generateTranspose(), generateTranspose(), generateTranspose()) }

        Text("${randomStrings[0]} ${randomStrings[1]} ${randomStrings[2]}")

        Button(onClick = {
            randomStrings.forEachIndexed { index, _ ->
                randomStrings[index] = generateTranspose()
            }
        }) {
            Text("Randomize")
        }
    }
}

fun generateTranspose(): String {
    return when (Random.nextInt(1, 80)) {
        in 1..20 -> "Seven"
        in 21..40 -> "Shark"
        in 41..60 -> "Smile"
        else -> "Gold"
    }
}
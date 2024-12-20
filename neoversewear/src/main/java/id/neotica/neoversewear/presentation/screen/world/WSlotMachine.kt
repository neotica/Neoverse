package id.neotica.neoversewear.presentation.screen.world

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Text
import id.neotica.neoversewear.presentation.components.CircularListWear
import id.neotica.neoversewear.presentation.components.NeoWButton
import id.neotica.neoversewear.presentation.components.SpacerItem
import id.neotica.ui.components.SlideInto
import id.neotica.ui.theme.DarkPrimaryTransparent
import id.neotica.world.utils.generateTranspose
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.security.SecureRandom

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun WSlotMachine(modifier: Modifier = Modifier, viewModel: WBarViewModel = koinViewModel()) {
    Column(
        Modifier
            .padding(20.dp)
            .fillMaxWidth()) {
        val randomNum = viewModel.randomNum.collectAsState()
        val randomNum2 = viewModel.randomNum2.collectAsState()
        val randomNum3 = viewModel.randomNum3.collectAsState()
        val randomStrings1 = remember {
            mutableStateListOf(
                randomNum.value,
                randomNum2.value,
                randomNum3.value
            )
        }
        val randomStrings2 = remember {
            mutableStateListOf(
                randomNum.value,
                randomNum2.value,
                randomNum3.value
            )
        }
        val randomStrings3 = remember {
            mutableStateListOf(
                randomNum.value,
                randomNum2.value,
                randomNum3.value
            )
        }

        var onClick by remember { mutableStateOf(false) }
        var delayedClick by remember { mutableStateOf(true) }
        var stopEnabled by remember { mutableStateOf(true) }

        var giveAura by remember { mutableStateOf(false) }

        val numbComb = "${randomStrings1[0]}${randomStrings2[1]}${randomStrings3[2]}"

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val cardColorsPrime = CardColors(
                containerColor = DarkPrimaryTransparent,
                contentColor = Color.White,
                disabledContentColor = DarkPrimaryTransparent,
                disabledContainerColor = DarkPrimaryTransparent
            )
            Card(
                colors = cardColorsPrime
            ) {
                Box {
                    Column(
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row(Modifier.height(48.dp)) {
                            CircularListWear(
                                items = randomStrings1,
                                scroll = onClick,
                                num1 = 3,
                                num2 = 2
                            )
                            CircularListWear(
                                items = randomStrings2,
                                scroll = onClick
                            )
                            CircularListWear(
                                items = randomStrings3,
                                scroll = onClick,
                                num2 = 3
                            )
                        }
                    }
                    SlideInto(
                        delayedClick,
                        outTime = 0
                    ) {
                        Surface (color = DarkPrimaryTransparent) { Text(numbComb) }
                    }
                }
            }
            val scope = rememberCoroutineScope()
            SpacerItem()
            Box(Modifier.width(218.dp)) {
                SlideInto(onClick) {
                    NeoWButton("Stop", enabled = stopEnabled, negative = true) {
                        viewModel.getAuraPoints(numbComb)
                        viewModel.sendAuraPoints(viewModel.auraPoints.value)
                        stopEnabled = false
                        scope.launch {
                            delay(500)
                            onClick = !onClick
                            delayedClick = true
                        }
                    }
                }
                SlideInto(!onClick) {
                    NeoWButton("Play", enabled = delayedClick) {
                        onClick = !onClick
                        delayedClick = false
                        giveAura = true
                        stopEnabled = true
                        randomStrings1.forEachIndexed { index, _ ->
                            randomStrings1[index] = generateTranspose(SecureRandom())
                        }
                        randomStrings2.forEachIndexed { index, _ ->
                            randomStrings2[index] = generateTranspose(SecureRandom())
                        }
                        randomStrings3.forEachIndexed { index, _ ->
                            randomStrings3[index] = generateTranspose(SecureRandom())
                        }
                    }
                }
            }
        }
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            val auraPoint = viewModel.auraPoints.value.toString()
            if (delayedClick) {
                val displayAuraPoint = if (auraPoint.contains("-")) auraPoint else "+$auraPoint"
                Text("$displayAuraPoint auras")
            } else {
                Text("")
            }
        }
    }
}
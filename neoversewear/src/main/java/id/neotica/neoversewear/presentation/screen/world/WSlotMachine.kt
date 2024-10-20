package id.neotica.neoversewear.presentation.screen.world

import android.annotation.SuppressLint
import android.util.Log
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Text
import id.neotica.neoversewear.presentation.navigation.SpacerItem
import id.neotica.ui.components.SlideInto
import id.neotica.ui.theme.DarkPrimaryTransparent
import id.neotica.world.feat.game.components.CircularList
import id.neotica.world.utils.generateTranspose
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.security.SecureRandom

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun WSlotMachine(modifier: Modifier = Modifier, viewModel: WBarViewModel = koinViewModel()) {
    Column(Modifier.padding(20.dp)) {
        val randomNum = viewModel.randomNum.collectAsState()
        val randomStrings = remember {
            mutableStateListOf(
                randomNum.value,
                randomNum.value,
                randomNum.value
            )
        }

        var onClick by remember { mutableStateOf(false) }
        var delayedClick by remember { mutableStateOf(true) }
        var stopEnabled by remember { mutableStateOf(true) }

        var giveAura by remember { mutableStateOf(false) }

        val numbComb = "${randomStrings[0]}${randomStrings[1]}${randomStrings[2]}"

        Row {

        }
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
                    Column {
                        Row(Modifier.height(40.dp)) {
                            CircularList(
                                items = randomStrings,
                                scroll = onClick,
                                num1 = 3,
                                num2 = 2
                            )
                            CircularList(
                                items = randomStrings,
                                scroll = onClick
                            )
                            CircularList(
                                items = randomStrings,
                                scroll = onClick,
                                num2 = 3
                            )
                        }
                    }
                    SlideInto(
                        delayedClick,
                        outTime = 0
                    ) {
                        Card(colors = cardColorsPrime) { Text(numbComb) }
                    }
                }
            }
            val scope = rememberCoroutineScope()
            SpacerItem()
            Box(Modifier.width(218.dp)) {
                SlideInto(onClick) {
                    Button(
                        onClick = {
                            viewModel.getAuraPoints(numbComb)
                            viewModel.sendAuraPoints(viewModel.auraPoints.value)
                            stopEnabled = false
                            scope.launch {
                                delay(500)
                                onClick = !onClick
                                delayedClick = true
                            }
                        },
                        enabled = stopEnabled
                    ) { Text("Stop") }
                }
                SlideInto(!onClick) {
                    Button(
                        onClick = {
                            onClick = !onClick
                            delayedClick = false
                            giveAura = true
                            stopEnabled = true
                            randomStrings.forEachIndexed { index, _ ->
                                randomStrings[index] = generateTranspose(SecureRandom())
                            }
                        },
                        enabled = delayedClick
                    ) {
                        Text("Play")
                    }
                }
            }
        }
        Log.d("slot", "numb$numbComb")
        Box {
            SlideInto(delayedClick) {
                val auraPoint = viewModel.auraPoints.value.toString()
                if (!auraPoint.contains("-")) {
                    Text("+${auraPoint} auras")
                } else {
                    Text("$auraPoint auras")
                }
            }
            Text("")
        }
    }
}
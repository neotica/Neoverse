package id.neotica.world.feat.game.components

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import id.neotica.world.feat.game.TavernViewModel
import id.neotica.world.utils.generateTranspose
import id.neotica.ui.components.NeoButton
import id.neotica.ui.components.SlideInto
import id.neotica.ui.theme.DarkPrimaryTransparent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.security.SecureRandom

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun SlotMachine(modifier: Modifier = Modifier, viewModel: TavernViewModel = koinViewModel()) {
    Column(Modifier.padding(20.dp)) {
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

        var onClick by rememberSaveable { mutableStateOf(false) }
        var delayedClick by rememberSaveable { mutableStateOf(true) }
        var stopEnabled by rememberSaveable { mutableStateOf(true) }

        var giveAura by rememberSaveable { mutableStateOf(false) }

        val numbComb = "${randomStrings1[0]}${randomStrings2[1]}${randomStrings3[2]}"

        val haptic = LocalHapticFeedback.current
        var onPressed by rememberSaveable { mutableStateOf(false) }
        if (onPressed) haptic.performHapticFeedback(HapticFeedbackType.LongPress)

        Box {
            SlideInto(delayedClick) {
                Text("You received ${viewModel.auraPoints.value} auras")
            }
            Text("")
        }
        Row {
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
                        Row(
                            modifier = Modifier
                                .height(80.dp)
                                .pointerInput(Unit) {
                                    awaitPointerEventScope {
                                        while (true) {
                                            awaitPointerEvent(pass = PointerEventPass.Initial)
                                                .changes
                                                .forEach(PointerInputChange::consume)
                                        }
                                    }
                                }
                        ) {
                            CircularList(
                                items = randomStrings1,
                                scroll = onClick,
                                num1 = 3,
                                num2 = 2
                            )
                            CircularList(
                                items = randomStrings2,
                                scroll = onClick
                            )
                            CircularList(
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
                        Card(colors = cardColorsPrime) { Text(numbComb) }
                    }
                }
            }
            Text("<- Good luck!")
        }

        val scope = rememberCoroutineScope()

        Row(Modifier.width(224.dp)) {
            NeoButton(
                text="Play",
                enabled = delayedClick,
            ) {
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
                onPressed = true
                viewModel.getItemEvent("Play", "Button")
            }

            Spacer(Modifier.padding(4.dp))
            SlideInto(onClick) {
                NeoButton(
                    text="Stop!",
                    enabled = stopEnabled
                ) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    viewModel.getAuraPoints(numbComb)
                    viewModel.sendAuraPoints(viewModel.auraPoints.value)
                    stopEnabled = false
                    scope.launch {
                        delay(500)
                        onClick = !onClick
                        delayedClick = true
                    }
                    viewModel.getItemEvent("Stop", "Button")
                }
            }
        }
        Log.d("slot", "numb$numbComb")

    }
}
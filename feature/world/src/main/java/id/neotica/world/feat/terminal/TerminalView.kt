package id.neotica.world.feat.terminal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import id.neotica.ui.R
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.droidcore.NeoSearchBar
import id.neotica.ui.navigation.RootScreen
import id.neotica.ui.navigation.Screen
import id.neotica.ui.navigation.WorldScreen
import id.neotica.ui.navigation.navigateBack
import id.neotica.ui.theme.DarkPrimaryTransparent80
import id.neotica.ui.theme.NeoPadding
import id.neotica.world.utils.GotoCommand
import id.neotica.world.utils.helpCmd
import id.neotica.world.utils.helpGoto
import id.neotica.world.utils.helpMath

@Composable
fun TerminalView(navController: NavController,) {
    val commandText = remember { mutableStateOf("") }
    var onPressed by remember { mutableStateOf(false) }

    val answerList = remember { mutableStateListOf<String>() }
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) { answerList.add(helpCmd) }

    LaunchedEffect(onPressed) {
        if (answerList.size != 0) listState.scrollToItem(answerList.size - 1)
        if (onPressed) {
            onPressed = false
            when {
                commandText.value.startsWith("goto ") -> goto(commandText.value.removePrefix("goto ").trim(), navController)
                commandText.value.startsWith("print(\"") && commandText.value.endsWith("\")") -> {
                    val contentToPrint = commandText.value
                        .removePrefix("print(\"")
                        .removeSuffix("\")")
                    answerList.add(contentToPrint) // Add the output inside print
                }
                commandText.value.toIntOrNull() != null -> {}
                commandText.value.matches(Regex("""(\d+)([+\-*/])(\d+)""")) -> {
                    try {
                        val result = evaluateExpression(commandText.value)
                        answerList.add("Result: $result")
                    } catch (e: Exception) {
                        answerList.add("Invalid arithmetic expression.")
                    }
                }
                else -> when (commandText.value) {
                    "help" -> answerList.add(helpCmd)
                    "help-math" -> answerList.add(helpMath)
                    "help-goto" -> answerList.add(helpGoto)
                    "" -> answerList.add("Input is empty.")
                    "lol" -> {
                        val lolResponses = listOf(
                            "Heh heh heh.",
                            "A most amusing predicament.",
                            "Indeed, a most humorous turn of events.",
                            "A wicked grin spreads across my face.",
                            "Gnawing laughter escapes my lips.",
                            "A most delightful display of folly!",
                            "Such mirth! I shall savor this moment."
                        )
                        answerList.add(lolResponses.random())
                    }
                    "clear" -> answerList.clear()
                    "exit" -> navController.navigateBack()
                    else -> answerList.add("Unknown command: ${commandText.value}")
                }
            }
            commandText.value = ""
        }
    }

    BasicScaffold("Terminal", fullScreen = true) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            LazyColumn(
                contentPadding = PaddingValues(
                    top = NeoPadding.topBarPadding,
                    bottom = 46.dp + NeoPadding.systemBarPadding,
                ),
                state = listState,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(answerList) {
                    Card(
                        colors = CardDefaults.cardColors().copy(containerColor = DarkPrimaryTransparent80)
                    ) {
                        Text(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            text = it
                        )
                    }
                }
            }
            Row(
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = NeoPadding.systemBarPadding)
            ) {
                NeoSearchBar(
                    value = commandText,
                    label = "Command",
                    imeAction = ImeAction.Send,
                    imeSend = { onPressed = true },
                    leadingIcon = {
                        Text(
                            text = "/",
                            modifier = Modifier.padding(horizontal =  16.dp)
                        )
                    },
                    trailingIcon = {
                        IconButton (
                            modifier = Modifier.padding(0.dp),
                            onClick = {
                                onPressed = true
                            },
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_send),
                                contentDescription = "",
                                tint = White
                            )
                        }
                    }
                )
            }
        }
    }
}

fun evaluateExpression(input: String): Double {
    val regex = Regex("""(\d+)([+\-*/])(\d+)""")
    val matchResult = regex.matchEntire(input)
        ?: throw IllegalArgumentException("Invalid format")

    val (num1, operator, num2) = matchResult.destructured
    val a = num1.toDouble()
    val b = num2.toDouble()

    return when (operator) {
        "+" -> a + b
        "-" -> a - b
        "*" -> a * b
        "/" -> a / b
        else -> throw IllegalArgumentException("Unsupported operator")
    }
}

private fun goto(go: String, navController: NavController) {
    val destinations = mapOf(
        GotoCommand().profile to Screen.ProfileScreen,
        GotoCommand().superAdmin to WorldScreen.SuperAdminScreen,
        GotoCommand().home to Screen.HomeScreen,
        GotoCommand().addChat to Screen.AddChatScreen,
        GotoCommand().rootHome to RootScreen.HomeNav,
        GotoCommand().rootWorld to RootScreen.WorldNav,
        GotoCommand().rootAuth to RootScreen.AuthNav,
        GotoCommand().tavern to WorldScreen.TavernScreen,
        GotoCommand().leaderboards to WorldScreen.LeaderBoardsScreen,
        GotoCommand().raven to WorldScreen.RavenScreen,
        GotoCommand().ravenChat to WorldScreen.RavenChatScreen,
        GotoCommand().merchant to WorldScreen.MerchantScreen
    )

    destinations[go]?.let { navController.navigate(it) }
}
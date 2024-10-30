package id.neotica.ui.components

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import id.neotica.ui.R
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicScaffold(
    topBarTitle: String,
    enableActionBar: Boolean = false,
    modifier: Modifier = Modifier,
    navController: NavController? = null,
    paddingValues: PaddingValues = PaddingValues(),
    paddingValuesBottomOnly: Boolean = true,
    fullScreen: Boolean = false,
    enableEye: Boolean = false,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    contentInset: WindowInsets = ScaffoldDefaults.contentWindowInsets,
    content: @Composable () -> Unit
) {
    Scaffold(
        modifier = Modifier.padding(),
        contentWindowInsets = contentInset,
        topBar = {
            TopAppBar(
                title = {
                    Text(topBarTitle)
                },
                modifier = Modifier,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Transparent,
                    titleContentColor = White,
                ),
                navigationIcon = {
                    if (!enableActionBar) {
                    } else {
                        IconButton(onClick = {
                            navController?.navigateBack()
                        }
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_back),
                                contentDescription = ""
                            )
                        }
                    }
                },
                actions = {
                    val eyeState = remember { mutableStateOf(false) }
                    val blinkCount = remember { mutableIntStateOf(0) }

                    if (enableEye) {
                        LaunchedEffect(eyeState.value) {
                            if (eyeState.value) {
                                val randomDelay = listOf(1000L, 2000L, 3000L, 500L, 5000L, 10000L).random()
                                delay(randomDelay)
                                eyeState.value = false
                            }
                        }
                        if (blinkCount.intValue >= 100) {
                            Toast.makeText(LocalContext.current, "Stop that.", Toast.LENGTH_SHORT).show()
                            blinkCount.intValue = 0
                        }
                        if (!eyeState.value) {
                            IconButton(
                                onClick = {
                                    val randomIntVal = listOf(
                                        1, 1, 1, 1, 1, 1, 1, 1, 1,
                                        0, 2, 3, 0, 5, 6, 7, 8, 0,
                                        10, 20,
                                    ).random()
                                    eyeState.value = !eyeState.value
                                    blinkCount.intValue += randomIntVal
                                }
                            ) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(R.drawable.ic_eye_open),
                                    contentDescription = "",
                                    tint = White
                                )
                            }
                        } else {
                            IconButton(
                                onClick = {eyeState.value = !eyeState.value}
                            ) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(R.drawable.ic_eye_closed),
                                    contentDescription = ""
                                )
                            }
                        }
                    }
                }
            )
        },
    ) {
        if (!fullScreen) {
            Column(
                modifier = Modifier
                    .padding(
                        if (paddingValuesBottomOnly) {
                            PaddingValues(bottom = paddingValues.calculateBottomPadding())
                        } else {
                            paddingValues
                        }
                    )
                    .fillMaxSize(),
                horizontalAlignment = horizontalAlignment
            ) {
                Column(
                    modifier = Modifier
                        .padding(it)
                        .fillMaxSize(),
                    horizontalAlignment = horizontalAlignment
                ) {
                    content()
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                horizontalAlignment = horizontalAlignment
            ) { content() }
        }
    }
}

private val NavController.canGoBack: Boolean
    get() = currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED

private fun NavController.navigateBack() {
    if (canGoBack) {
        popBackStack()
    }
}
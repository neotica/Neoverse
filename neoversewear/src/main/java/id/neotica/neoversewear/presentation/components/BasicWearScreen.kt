package id.neotica.neoversewear.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyListScope
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun BasicWearScreen(modifier: Modifier = Modifier, content: ScalingLazyListScope.() -> Unit) {
    val scalingLazyListState = androidx.wear.compose.foundation.lazy.rememberScalingLazyListState()
    val height = remember { mutableIntStateOf(1) }
    var isScrollIndicatorVisible by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()


// Monitor the scroll state and set visibility with delay
    LaunchedEffect(scalingLazyListState.isScrollInProgress) {
        if (scalingLazyListState.isScrollInProgress) {
            isScrollIndicatorVisible = true // Show the indicator while scrolling
        } else {
            coroutineScope.launch {
                delay(2000) // Wait for 2 seconds
                isScrollIndicatorVisible = false // Hide the indicator
            }
        }
    }

    Scaffold(
        modifier = Modifier.onGloballyPositioned { height.intValue = it.size.height },
        positionIndicator = {
            AnimatedVisibility(
                isScrollIndicatorVisible,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                // Hack to ALWAYS show the scrollbars...Google happy now
                PositionIndicator(
                    state = AlwaysShowScrollBarScalingLazyColumnStateAdapter(
                        state = scalingLazyListState,
                        viewportHeightPx = height,
                    ),
                    //region Original values from PositionIndicator
                    indicatorHeight = 50.dp,
                    indicatorWidth = 4.dp,
                    paddingHorizontal = 5.dp,
                    reverseDirection = false,
                    //endregion
                )
            }
        }
    ) {
        ScalingLazyColumn(
            modifier = Modifier
            .fillMaxWidth(),
            userScrollEnabled = true,
            state = scalingLazyListState,
            verticalArrangement = Arrangement.Center,
        ) {
            content()
        }
    }
}
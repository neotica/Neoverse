package id.neotica.ui.components.scroll

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow

@Composable
fun scrollUpValue(listState: LazyListState): Boolean {
    /**This is the state to make the top app bar disappear/appears on lazyList scroll**/
    var isScrollingUp by remember { mutableStateOf(true) }
    var previousOffset by remember { mutableIntStateOf(0) }
    val scrollThreshold = 100

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemScrollOffset }
            .collect { currentOffset ->
                if (kotlin.math.abs(currentOffset - previousOffset) > scrollThreshold) {
                    isScrollingUp = currentOffset < previousOffset
                    previousOffset = currentOffset
                }
            }
    }

    return isScrollingUp
}
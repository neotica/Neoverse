package id.neotica.world.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier

@Composable
fun CircularList(
    items: List<String>,
    scroll: Boolean = false,
    num1: Int = 5,
    num2: Int = 1,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState(Int.MAX_VALUE / num1)

    LaunchedEffect(scroll) {
        while (scroll) {
            listState.animateScrollToItem(listState.firstVisibleItemIndex + num2)
        }
    }

    LazyColumn(state = listState, modifier = modifier) {
        items(
            count = Int.MAX_VALUE,
            key = { index ->
                "${index}-${index % items.size}"
                  },
            itemContent = {
                items.forEach {
                    Text(it)
                }
            }
        )
    }
}
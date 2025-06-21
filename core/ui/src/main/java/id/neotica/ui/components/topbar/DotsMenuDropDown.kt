package id.neotica.ui.components.topbar

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import id.neotica.ui.R
import id.neotica.ui.components.DropdownItem
import id.neotica.ui.theme.DarkPrimaryTransparent2

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DotsMenuDropDown(item: List<DotsMenuItem>, onLongClick: () -> Unit? = {}) {
    val expanded = remember { mutableStateOf(false) }
    Box {
        Icon(
            modifier = Modifier
                .minimumInteractiveComponentSize()
                .combinedClickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = true, color = White),
                    onClick = {
                        expanded.value = !expanded.value
                    },
                    onLongClick = { onLongClick() },
                ),
            imageVector = ImageVector.vectorResource(R.drawable.three_dots),
            contentDescription = "",
            tint = White
        )
        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false },
            containerColor = DarkPrimaryTransparent2,
        ) {
            item.forEach {
                DropdownItem(it.title, White, it.custom) {
                    expanded.value = false
                    it.action()
                }
            }
        }
    }
}

data class DotsMenuItem(
    val title: String,
    val custom: (@Composable () -> Unit)? = null,
    val action: () -> Unit
)
package id.neotica.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import id.neotica.droidcore.component.alert.NeoAlert
import id.neotica.ui.theme.DarkPrimaryTransparent

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProfileCard(
    name: String,
    image: String,
    size: Dp = 100.dp,
    paddingValues: PaddingValues = PaddingValues(),
    content: @Composable () -> Unit = {},
    alertTitle: String = "",
    alertButton: String = "",
    enableLongClick: Boolean = false,
    onLongClick: () -> Unit = {},
    onClick: () -> Unit = {}
) {
    val openDialog = remember { mutableStateOf(false) }

    val cardColorsPrime = CardColors(
        containerColor = DarkPrimaryTransparent,
        contentColor = Color.White,
        disabledContentColor = DarkPrimaryTransparent,
        disabledContainerColor = DarkPrimaryTransparent
    )

    val longClick: (() -> Unit)? = if (enableLongClick) {
        { openDialog.value = true }
    } else {
        null
    }

    if (openDialog.value) {
        NeoAlert(
            openDialog = openDialog,
            title = alertTitle,
            backButton = "Cancel",
            confirmButton = {
                NeoButton(
                    alertButton,
                    negative = true
                ) {
                    onLongClick()
                    openDialog.value = false
                }
            }
        )
    }

    Card(
        colors = cardColorsPrime,
        modifier = Modifier
            .combinedClickable(
                onClick = { onClick() },
                onLongClick = longClick,
            )
            .padding(paddingValues),
    ) {
        Column(
            Modifier.padding(8.dp),
            verticalArrangement = Arrangement.Center
        ) {
            ImageCoil(
                url = image,
                round = true,
                size = size
            )
            Text(
                text = name,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            content()
        }
    }
}
package id.neotica.neoverse.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import id.neotica.neoverse.presentation.ui.theme.DarkPrimaryTransparent

@Composable
fun ProfileCard(
    name: String,
    image: String,
    modifier: Modifier = Modifier,
    size: Dp = 100.dp,
    paddingValues: PaddingValues = PaddingValues(),
    content: @Composable () -> Unit = {},
    onClick: () -> Unit = {}
) {
    val cardColorsPrime = CardColors(
        containerColor = DarkPrimaryTransparent,
        contentColor = Color.White,
        disabledContentColor = DarkPrimaryTransparent,
        disabledContainerColor = DarkPrimaryTransparent
    )

    Card(
        colors = cardColorsPrime,
        modifier = Modifier.padding(paddingValues),
        onClick = { onClick() },
    ) {
        Column(
            Modifier.padding(8.dp),
            verticalArrangement = Arrangement.Center
        ) {
            ImageCoil(
                url = image,
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
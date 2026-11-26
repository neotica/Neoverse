package id.neotica.ui.shared.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.painterResource
import id.neotica.res.MR
import id.neotica.ui.shared.theme.FontSize
import id.neotica.ui.shared.theme.Transparent
import id.neotica.ui.shared.theme.TransparentText40

@Composable
fun ControlledNeoSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Search,
    imeSend: (() -> Unit)? = null,
    padding: PaddingValues = PaddingValues(),
    trailingIcon: (@Composable () -> Unit)? = null,
    label: String? = null,
) {
    Card(
        modifier
            .padding(padding)
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = TransparentText40,
                shape = RoundedCornerShape(12.dp),
            ),
        colors = CardDefaults.cardColors().copy(containerColor = Transparent),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(MR.images.ic_search),
                contentDescription = "",
                tint = TransparentText40,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .background(color = Transparent)
                    .fillMaxWidth()
                    .weight(1f),
                textStyle = TextStyle.Default.copy(
                    fontSize = FontSize.Normal,
                    color = White,
                ),
                maxLines = 1,
                keyboardOptions = KeyboardOptions(imeAction = imeAction),
                keyboardActions = if (imeSend != null) {
                    KeyboardActions(onSend = { imeSend() })
                } else {
                    KeyboardActions.Default
                },
                cursorBrush = SolidColor(White),
            ) { innerTextField ->
                Row(
                    Modifier.height(46.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box {
                        innerTextField()
                        if (value.isEmpty()) {
                            Text(
                                text = label.orEmpty(),
                                color = TransparentText40,
                                fontSize = FontSize.Normal,
                                maxLines = 1,
                            )
                        }
                    }
                }
            }
            trailingIcon?.invoke()
        }
    }
}

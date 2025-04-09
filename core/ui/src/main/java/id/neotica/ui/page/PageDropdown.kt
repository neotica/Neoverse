package id.neotica.ui.page

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import id.neotica.ui.R
import id.neotica.ui.components.DropdownItem
import id.neotica.ui.theme.DarkPrimaryTransparent2

@Composable
fun PageDropdown(
    currentValue: Int, count: Int,
    onValueChange: (Int) -> Unit
) {
    var dropDown by remember { mutableStateOf(false) }
//    var dropDownValue by remember { mutableIntStateOf(0) }

    Card {
        Row(
            modifier= Modifier
                .clickable { dropDown = true }
                .padding(8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text("$currentValue")
            Icon(painterResource(
                if (dropDown) R.drawable.ic_arrow_drop_down else R.drawable.ic_arrow_drop_up),
                contentDescription = "Dropdown to select page"
            )
        }
        if (dropDown) {
            Popup(
                onDismissRequest = { dropDown = false }
            ) {
                Card(
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(8.dp),
                    colors = CardDefaults.cardColors().copy(containerColor = DarkPrimaryTransparent2),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        Modifier.wrapContentWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        repeat(count) {
                            DropdownItem("${it + 1}") {
                                onValueChange(it)
                                dropDown = false
//                                dropDownValue = it
                            }
                        }
                    }
                }
            }
        }
    }
}
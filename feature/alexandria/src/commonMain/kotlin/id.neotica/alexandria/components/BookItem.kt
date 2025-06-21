package id.neotica.alexandria.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import id.neotica.routes.AlexandriaScreen
import id.neotica.ui.shared.theme.DarkPrimaryTransparent80

@Composable
fun BookItem(
    title: String,
    route: AlexandriaScreen,
    navController: NavController,
) {
    Card(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, bottom = 8.dp, top = 8.dp)
            .clip(RoundedCornerShape(16.dp))
        ,
        colors = CardDefaults.cardColors().copy(containerColor = DarkPrimaryTransparent80)
    ) {
        Row(
            modifier = Modifier
                .clickable { navController.navigate(route) }
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }
    }
}
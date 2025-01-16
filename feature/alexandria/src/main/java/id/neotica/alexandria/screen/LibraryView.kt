package id.neotica.alexandria.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.navigation.AlexandriaScreen
import id.neotica.ui.theme.DarkPrimaryTransparent80

@Composable
fun LibraryView(
    navController: NavController
) {
    BasicScaffold(
        "The Library of Thoth",
        navController = navController,
        enableActionBar = true
    ) {
        Card(
            modifier = Modifier
                .clickable { navController.navigate(AlexandriaScreen.GoetiaScreen) }
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors().copy(containerColor = DarkPrimaryTransparent80)
        ) {
            Row(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Goetia")
            }

        }
    }
}
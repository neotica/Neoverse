package id.neotica.alexandria.screen.admin.warning

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import id.neotica.ui.components.LoadingIndicator
import id.neotica.routes.AlexandriaScreen
import id.neotica.ui.theme.DarkPrimaryTransparent80
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WarningHubView(
    navController: NavController,
    viewModel: WarningHubViewModel = koinViewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val warnings by viewModel.warning.collectAsState()
    val message by viewModel.message.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(message) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    LazyColumn {
        warnings?.let {
            items(it) {
                Card(
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, bottom = 8.dp, top = 8.dp)
                        .clip(RoundedCornerShape(16.dp))
                    ,
                    colors = CardDefaults.cardColors().copy(containerColor = DarkPrimaryTransparent80)
                ) {
                    Column(
                        modifier = Modifier
                            .combinedClickable {
                                navController.navigate(AlexandriaScreen.WarningDetailScreen(it.id))
                            }
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(it.bookTitle)
                        Text("Id: ${it.id}")
                        Text("Message: ${it.message}")
                        Text("Appealed: ${it.isAppealed}")
                        Text("Approved: ${it.isApproved}")
                        Text("Version: ${it.version}")
                    }
                }
            }
        }
    }
    if (isLoading) Box(
        Modifier
            .fillMaxSize()
            .clickable(enabled = true) { },
    ) { LoadingIndicator() }
}
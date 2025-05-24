package id.neotica.alexandria.screen.admin.warning.detail

import android.widget.Toast
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import id.neotica.ui.components.BasicScaffold
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WarningDetailView(
    navController: NavController,
    viewModel: WarningDetailViewModel = koinViewModel()
) {
    val warning by viewModel.warning.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val message by viewModel.message.collectAsState()

    val context = LocalContext.current

    var isChecked by remember { mutableStateOf(false) }

    LaunchedEffect(message) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    BasicScaffold(
        "Warning Detail",
        isLoading = isLoading,
    ) {
        warning?.let {
            Text(it.bookTitle)
            Text("Id: ${it.id}")
            Text("Message: ${it.message}")
            Text("Appealed: ${it.isAppealed}")
            Text("Approved: ${it.isApproved}")
            Text("Version: ${it.version}")
        }
    }
}
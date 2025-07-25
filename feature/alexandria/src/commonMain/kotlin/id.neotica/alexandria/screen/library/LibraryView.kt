package id.neotica.alexandria.screen.library

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import id.neotica.droidcore.component.alert.NeoToast
import id.neotica.routes.AlexandriaScreen
import id.neotica.routes.Screen
import id.neotica.state.AppVariant
import id.neotica.ui.shared.components.BasicScaffold
import id.neotica.ui.shared.components.LoadingIndicator
import id.neotica.ui.shared.components.NeoButton
import id.neotica.ui.shared.components.topbar.DotsMenuDropDown
import id.neotica.ui.shared.components.topbar.DotsMenuItem
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryView(
    navController: NavController,
    appVariant: AppVariant,
    viewModel: LibraryViewModel = koinViewModel()
) {
    val isLoading by viewModel.loading.collectAsState()
    val books by viewModel.books.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val toast = remember { mutableStateOf(false) }
    val toastMessage = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        if (viewModel.isNotLogged.value) {
            navController.navigate(Screen.LoginScreen) {
                popUpTo(0) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            toast.value = true
            toastMessage.value = it
        }
    }

    BasicScaffold(
        "Hadall Archive",
        navController = navController,
        enableActionBar = when {
            appVariant.appName == "Alexandria" -> false
            else -> true
        },
        trailingIcon = {
            DotsMenuDropDown(
                item = listOf(
                    DotsMenuItem("Author's Page") {
                        navController.navigate(AlexandriaScreen.UserLibraryScreen)
                    },
                    DotsMenuItem("Admin Page") {
                        viewModel.getPermission {
                            navController.navigate(AlexandriaScreen.AdminScreen)
                        }
                    }
                ),
            )
        }
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            errorMessage?.let {
                NeoButton("Reload") {
                    viewModel.getBooks()
                }
            }
            if (books.isEmpty() && !isLoading) {
                Text("Book is empty")
            }
        }

        LibraryChildView(navController, books)
    }
    if (isLoading) Box(
        Modifier
            .fillMaxSize()
            .clickable(enabled = true) { },
    ) { LoadingIndicator() }
    if (toast.value) NeoToast(toastMessage.value, toast)
}
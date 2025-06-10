package id.neotica.alexandria.screen.library

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
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
//    val context = LocalContext.current
    val errorMessage by viewModel.errorMessage.collectAsState()

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
//            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    BasicScaffold(
        "The Library of Thoth",
        navController = navController,
        enableActionBar = when {
            appVariant.appName == "Alexandria" -> false
            else -> true
        },
        trailingIcon = {
            DotsMenuDropDown(
                item = listOf(
                    DotsMenuItem("Author's Page") {
                        viewModel.getPermission {
                            navController.navigate(AlexandriaScreen.UserLibraryScreen)
                        }
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
        errorMessage?.let {
            NeoButton("Reload") {
                viewModel.getBooks()
            }
        }
        LibraryChildView(navController, books)
    }
    if (isLoading) Box(
        Modifier
            .fillMaxSize()
            .clickable(enabled = true) { },
    ) { LoadingIndicator() }
}
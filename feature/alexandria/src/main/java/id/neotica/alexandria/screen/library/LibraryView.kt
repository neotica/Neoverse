package id.neotica.alexandria.screen.library

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.LoadingIndicator
import id.neotica.ui.components.NeoButton
import id.neotica.ui.components.topbar.DotsMenuDropDown
import id.neotica.ui.components.topbar.DotsMenuItem
import id.neotica.ui.navigation.AlexandriaScreen
import id.neotica.ui.navigation.Screen
import id.neotica.ui.state.AppVariant
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LibraryView(
    navController: NavController,
    appVariant: AppVariant,
    viewModel: LibraryViewModel = koinViewModel()
) {
    var isLoading = viewModel.loading.collectAsState().value //by remember { mutableStateOf(false) }
    var books = viewModel.books.collectAsState().value
    val context = LocalContext.current
    val errorMessage = viewModel.errorMessage.collectAsState().value

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
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
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
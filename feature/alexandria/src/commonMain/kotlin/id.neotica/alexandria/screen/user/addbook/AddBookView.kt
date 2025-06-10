package id.neotica.alexandria.screen.user.addbook

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.icerock.moko.resources.compose.painterResource
import id.neotica.domain.ApiResult
import id.neotica.domain.model.alexandria.Book
import id.neotica.droidcore.component.textfield.NeoTextField
import id.neotica.rickpository.res.MR
import id.neotica.ui.shared.components.BasicScaffold
import id.neotica.ui.shared.components.DropdownItem
import id.neotica.ui.shared.components.LoadingIndicatorBool
import id.neotica.ui.shared.components.NeoButton
import id.neotica.routes.AlexandriaScreen
import id.neotica.ui.shared.theme.DarkPrimaryTransparent2
import id.neotica.ui.shared.theme.NeoPadding
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AddBookView(
    navController: NavController,
    viewModel: AddBookViewModel = koinViewModel()
) {
    var isLoading by remember { mutableStateOf(false) }
//    val context = LocalContext.current

    var bookState by remember { mutableStateOf<Book?>(null) }

    LaunchedEffect(Unit) {
        viewModel.toastMessage.collect {
//            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.isLoading.collect { isLoading = it }
    }

    LaunchedEffect(Unit) {
        viewModel.book.collect {
            if (it is ApiResult.Success) {
                bookState = it.data
                navController.navigate(AlexandriaScreen.EditBookScreen(it.data!!.id)) {
                    popUpTo(AlexandriaScreen.AddBookScreen) { inclusive = true }
                }
            }
        }
    }

    BasicScaffold(
        topBarTitle = "Content",
        fullScreen = true,
        enableActionBar = true,
        navController = navController,
    ){
        val title = remember { mutableStateOf("") }
        var language by remember { mutableStateOf("EN") }

        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(
                top = NeoPadding.topBarPadding,
                bottom = NeoPadding.systemBarPadding + 50.dp
            )
        ){
            item {
                var languageDropdown by remember { mutableStateOf(false) }
                LoadingIndicatorBool(isLoading) {
                    NeoTextField(title, label = "Title")
                    Box {
                        Row(
                            modifier= Modifier
                                .clickable { languageDropdown = true }
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text("Language: $language")
                            Icon(painterResource(
                                if (!languageDropdown) {
                                    MR.images.ic_arrow_drop_down
                                } else {
                                    MR.images.ic_arrow_drop_up
                                }
                            ),
                                contentDescription = "Dropdown to select page"
                            )
                        }
                        DropdownMenu(
                            languageDropdown,
                            onDismissRequest = { languageDropdown = false },
                            containerColor = DarkPrimaryTransparent2
                        ) {
                            DropdownItem("English") {
                                language = "EN"
                                languageDropdown = false
                            }
                            DropdownItem("Indonesia") {
                                language = "ID"
                                languageDropdown = false
                            }
                        }
                    }
                    NeoButton("Create Book") {
                        viewModel.addBook(title.value, language)
                    }
                }
            }
        }
    }
}
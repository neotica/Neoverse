package id.neotica.alexandria.screen.user.addbook

import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import id.neotica.domain.ApiResult
import id.neotica.domain.model.alexandria.Book
import id.neotica.droidcore.component.textfield.NeoTextField
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.LoadingIndicatorBool
import id.neotica.ui.components.NeoButton
import id.neotica.ui.navigation.AlexandriaScreen
import id.neotica.ui.theme.NeoPadding
import org.koin.androidx.compose.koinViewModel

@Composable
fun AddBookView(
    navController: NavController,
    viewModel: AddBookViewModel = koinViewModel()
) {
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    var bookState by remember { mutableStateOf<Book?>(null) }


    LaunchedEffect(Unit) {
        viewModel.book.collect {
            when (it) {
                is ApiResult.Loading -> isLoading = true
                is ApiResult.Success -> {
                    isLoading = false
                    bookState = it.data
                    navController.navigate(AlexandriaScreen.EditBookScreen(it.data!!.id)) {
                        popUpTo(AlexandriaScreen.AddBookScreen) { inclusive = true }
                    }
                    Toast.makeText(context, it.data.toString(), Toast.LENGTH_SHORT).show()
                }
                is ApiResult.Error -> isLoading = false
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
                LoadingIndicatorBool(isLoading) {
                    Text("Title: ")
                    NeoTextField(title)

                    NeoButton("Create Book") {
                        viewModel.addBook(title.value)
                    }
                }
            }
        }
    }
}
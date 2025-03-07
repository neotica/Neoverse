package id.neotica.alexandria.screen.goetia

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
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
import id.neotica.alexandria.components.ChapterContent
import id.neotica.alexandria.components.ChapterItem
import id.neotica.domain.ApiResult
import id.neotica.domain.model.alexandria.Spirits
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.LoadingIndicator
import id.neotica.ui.components.droidcore.NeoSearchBar
import id.neotica.ui.theme.DarkBackground
import id.neotica.ui.theme.NeoPadding
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoetiaView(
    navController: NavController,
    viewModel: GoetiaViewModel = koinViewModel()
) {
    val spiritList = remember { mutableStateOf<List<Spirits>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    val searchBar = remember { mutableStateOf("") }

    val sheetState = rememberModalBottomSheetState()
    var showSpiritSheet by remember { mutableStateOf(false) }
    var spiritDetail by remember {
        mutableStateOf(
            Spirits(
            id = 0,
            name = "",
            desc = "",
            imageUrl = ""
            )
        )
    }

    LaunchedEffect(Unit) {
        viewModel.spirits.collect { result ->
            when(result) {
                is ApiResult.Loading -> isLoading = true
                is ApiResult.Success -> {
                    isLoading = false
                    spiritList.value = result.data!!
                }
                is ApiResult.Error -> isLoading = false
            }
        }
    }

    if (showSpiritSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSpiritSheet = false },
            sheetState = sheetState,
            containerColor = DarkBackground
        ) {
            LazyColumn (
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                item {
                    ChapterContent(
                        title = "${spiritDetail.id}. ${spiritDetail.name}",
                        content = spiritDetail.desc,
                        imageUrl = spiritDetail.imageUrl
                    )
                }
            }
        }
    }

    BasicScaffold(
        "Goetia",
        fullScreen = true
    ) {
        if (isLoading) LoadingIndicator()
        Spacer(modifier = Modifier.padding(top = NeoPadding.topBarPadding))
        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            NeoSearchBar(searchBar, label = "Search Spirits")
        }


        LazyColumn(
            contentPadding = PaddingValues(
                bottom = NeoPadding.systemBarPadding
            )
        ) {
            val spiritsFilter = spiritList.value.filter {
                it.name.contains(searchBar.value, ignoreCase = true)
                        || it.altName?.contains(searchBar.value, ignoreCase = true) ?: false
            }
            items(spiritsFilter) {
                ChapterItem(
                    title = it.id.toString() + ". " + it.name,
                    imageUrl = it.imageUrl
                ) {
                    showSpiritSheet = true
                    spiritDetail = Spirits(
                        it.id,
                        it.name,
                        it.altName,
                        it.desc,
                        it.imageUrl
                    )
                }
            }
        }
    }
}
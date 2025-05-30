package id.neotica.alexandria.screen.admin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import id.neotica.alexandria.screen.admin.library.AdminLibraryView
import id.neotica.alexandria.screen.admin.warning.WarningHubView
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.theme.DarkBackground
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AdminView(navController: NavController, viewModel: AdminViewModel = koinViewModel()) {
    BasicScaffold(
        navController = navController,
        topBarTitle = "Admin",
        enableActionBar = true
    ) {
        val scope = rememberCoroutineScope()
        val pagerState = rememberPagerState(pageCount = { AdminTabs.entries.size })
        val selectedTabIndex = remember { derivedStateOf { pagerState.currentPage } }

        ScrollableTabRow(
            containerColor = DarkBackground,
            selectedTabIndex = selectedTabIndex.value,
            modifier = Modifier.fillMaxWidth()
        ) {
            AdminTabs.entries.forEachIndexed { index, tab ->
                Tab(
                    selected = index == selectedTabIndex.value,
                    onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                    text = { Text(tab.text) }
                )
            }
        }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Box {
                when (AdminTabs.entries[selectedTabIndex.value]) {
                    AdminTabs.Library -> AdminLibraryView(navController)
                    AdminTabs.WarningHub -> WarningHubView(navController)
                }
            }
        }
    }
}

enum class AdminTabs(val text: String) {
    Library("Library"),
    WarningHub("Warning Hub")
}
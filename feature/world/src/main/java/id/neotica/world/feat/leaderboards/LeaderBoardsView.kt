package id.neotica.world.feat.leaderboards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import id.neotica.domain.ApiResult
import id.neotica.domain.model.UserData
import id.neotica.domain.model.chat.ParentNeo
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.LoadingIndicatorBool
import id.neotica.ui.components.NeoButton
import id.neotica.ui.theme.NeoPadding
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LeaderBoardsView(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: LeaderBoardsViewModel = koinViewModel()
) {
    var isLoading by remember { mutableStateOf(false) }
    var allUser by remember { mutableStateOf<List<ParentNeo<UserData>>?>(emptyList()) }
    var onClick by remember { mutableStateOf(false) }

    LaunchedEffect(onClick) {
        viewModel.allUser.collect { result ->
            when(result) {
                is ApiResult.Loading -> isLoading = true
                is ApiResult.Success -> {
                    onClick = false
                    isLoading = false
                    allUser = result.data
                }
                is ApiResult.Error -> isLoading = false
            }
        }
    }

    BasicScaffold(
        topBarTitle = "Leaderboards \uD83C\uDFC6",
        horizontalAlignment = Alignment.CenterHorizontally,
        navController = navController,
        enableActionBar = true
    ) {
        Spacer(Modifier.padding(8.dp))
        if (!viewModel.hasJoinedLeaderBoards() && !isLoading) {
            NeoButton("Join Leaderboards") {
                viewModel.joinLeaderboards()
                onClick = true
            }
            Spacer(Modifier.padding(8.dp))
        }
        LoadingIndicatorBool(isLoading) {
            LazyColumn(
                contentPadding = PaddingValues(
                    bottom = NeoPadding.systemBarPadding + paddingValues.calculateBottomPadding()
                )
            ) {
                allUser?.let {
                    itemsIndexed(it) { index, child ->
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row {
                                Text("${index + 1}. ")
                                Text(child.value.world.username)
                            }
                            Text("${child.value.world.aura} Aura")
                        }
                    }
                }
            }
        }
    }
}
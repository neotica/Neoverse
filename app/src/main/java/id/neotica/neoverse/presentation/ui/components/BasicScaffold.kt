package id.neotica.neoverse.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import id.neotica.neoverse.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicScaffold(
    topBarTitle: String,
    enableActionBar: Boolean = false,
    modifier: Modifier = Modifier,
    navController: NavController? = null,
    paddingValues: PaddingValues = PaddingValues(),
    fullScreen: Boolean = false,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable () -> Unit
) {
    Scaffold(
        modifier = Modifier.padding(),
        topBar = {
            TopAppBar(
                title = {
                    Text(topBarTitle)
                },
                modifier = Modifier,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Transparent,
                    titleContentColor = Color.White,
                ),
                navigationIcon = {
                    if (!enableActionBar) {
                    } else {
                        IconButton(onClick = {
                            navController?.navigateBack()
                        }
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_back),
                                contentDescription = ""
                            )
                        }
                    }
                }
            )
        },
    ) {
        if (!fullScreen) {
            Column(
                modifier = Modifier.padding(it).fillMaxSize(),
                horizontalAlignment = horizontalAlignment
            ) { content() }
        } else {
            Column(
                modifier = Modifier.padding(paddingValues).fillMaxSize(),
                horizontalAlignment = horizontalAlignment
            ) { content() }
        }
    }
}

val NavController.canGoBack: Boolean
    get() = currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED

fun NavController.navigateBack() {
    if (canGoBack) {
        popBackStack()
    }
}
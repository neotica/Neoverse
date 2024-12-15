package id.neotica.world.feat.game

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import id.neotica.domain.ApiResult
import id.neotica.domain.model.chat.ParentNeo
import id.neotica.domain.model.tavern.TavernPost
import id.neotica.domain.utils.convertLongToTime
import id.neotica.droidcore.component.textfield.NeoTextField
import id.neotica.ui.R
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.LoadingIndicator
import id.neotica.ui.navigation.RootScreen
import id.neotica.ui.navigation.Screen
import id.neotica.ui.theme.DarkPrimary
import id.neotica.ui.theme.DarkPrimaryTransparent40
import id.neotica.ui.theme.Transparent
import id.neotica.world.feat.game.components.SlotMachine
import org.koin.androidx.compose.koinViewModel

@Composable
fun TavernView(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: TavernViewModel = koinViewModel()
) {

    var currentAuras by remember { mutableStateOf<Long?>(null) }
    var auraInit by remember { mutableStateOf(false) }
    val whisper = remember { mutableStateOf("") }
    val context = LocalContext.current

    var isLoading by remember { mutableStateOf(false) }
    var whispers by remember { mutableStateOf<List<ParentNeo<TavernPost>>?>(emptyList()) }
    var onClick by remember { mutableStateOf(false) }

    LaunchedEffect(onClick) {
        viewModel.whispers.collect { result ->
            when(result) {
                is ApiResult.Loading -> isLoading = true
                is ApiResult.Success -> {
                    onClick = false
                    isLoading = false
                    whispers = result.data
                }
                is ApiResult.Error -> isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        Log.d("auraInit", auraInit.toString())

        viewModel.totalAuras.collect { result ->
            Log.d("auraInit", result.errorMessage. toString())
            when(result) {
                is ApiResult.Loading -> {
                    auraInit = false
                }
                is ApiResult.Success -> {
                    currentAuras = result.data
                    if (result.data == null) auraInit = true
                    result.data?.let {
                        if (it <= 0) {
                            Toast.makeText(context, "Aura is below", Toast.LENGTH_SHORT).show()
                            navController.navigateUp()
                        }
                    }
                }
                is ApiResult.Error -> {
                    if (result.data == null) auraInit = true
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        if (!viewModel.isLoggedIn()) navController.navigate(Screen.LoginScreen) {
            popUpTo(RootScreen.HomeNav) {
                inclusive = true
            }
            launchSingleTop
        }
    }

    BasicScaffold(
        topBarTitle = "The Dragon Chest's Tavern",
        horizontalAlignment = Alignment.CenterHorizontally,
        navController = navController,
        enableActionBar = true
    ) {
        Text("Your Aura: ${currentAuras ?: ""}")

        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(Modifier.padding(vertical = 16.dp))
                Card(
                    colors = CardDefaults.cardColors().copy(
                        containerColor = Transparent
                    ),
                    border = BorderStroke(2.dp, DarkPrimary)
                ) {
                    SlotMachine(
                        Modifier
                            .fillMaxWidth()
                            .padding(30.dp))
                }
                Column(
                    Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
                ) {
                    Text("Every whisper costs you 5 aura.")
                    Row {
                        Box(Modifier.weight(2f)) {
                            NeoTextField(
                                value = whisper,
                                label = "Whisper",
                                trailingIcon = {
                                    Icon(
                                        modifier = Modifier.clickable {
                                            if (whisper.value.isEmpty()) {
                                                Toast.makeText(context, "Please enter a message.", Toast.LENGTH_SHORT).show()
                                            } else {
                                                viewModel.sendPost(whisper.value)
                                                whisper.value = ""
                                                onClick = true
                                            }
                                        },
                                        imageVector = ImageVector.vectorResource(R.drawable.add_chat),
                                        contentDescription = "",
                                        tint = White
                                    )
                                }
                            )
                        }
                    }
                }
            }

            whispers?.let {
                items(it) { items ->
                    val value = items.value
                    Card(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        colors = CardDefaults.cardColors().copy(containerColor = DarkPrimaryTransparent40)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(value.creatorName)
                            Text(value.message)
                            Text(convertLongToTime(
                                time = value.createdAt,
                                clean = true
                            ))
                        }
                    }
                }
            }
        }
    }
    if (isLoading) Box(Modifier.fillMaxSize()) { LoadingIndicator() }
}
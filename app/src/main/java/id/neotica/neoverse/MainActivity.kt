package id.neotica.neoverse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import id.neotica.neoverse.presentation.navigation.NavGraph
import id.neotica.neoverse.presentation.navigation.NavigationItem
import id.neotica.neoverse.presentation.navigation.RootScreen
import id.neotica.neoverse.presentation.navigation.Screen
import id.neotica.neoverse.presentation.ui.theme.NeoverseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NeoverseTheme {
                val navController = rememberNavController()

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                val navItems = listOf(
                    NavigationItem(
                        title = "Home",
                        icon = Icons.Default.Home,
                        screen = RootScreen.HomeNav.route
                    ),
                    NavigationItem(
                        title = "Chat",
                        icon = Icons.Default.AccountCircle,
                        screen = Screen.Chat.route
                    ),
                    NavigationItem(
                        title = "Settings",
                        icon = Icons.Default.Settings,
                        screen = Screen.Setting.route
                    )
                )
                val excludedRoutes = listOf(
                    Screen.Splash.route,
                    Screen.Login.route,
                    Screen.Register.route,
                )

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (currentRoute !in excludedRoutes) {
                            NavigationBar {
                                navItems.map {
                                    NavigationBarItem(
                                        selected = currentRoute == it.screen,
                                        onClick = {
                                            navController.navigate(it.screen) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                restoreState = true
                                                launchSingleTop = true
                                            }
                                        },
                                        icon = {
                                            Icon(imageVector = it.icon, contentDescription = it.title)
                                        },
                                        label = { Text(it.title) },
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    NavGraph(navController = navController, paddingValues = innerPadding)
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NeoverseTheme {
        Greeting("Android")
    }
}
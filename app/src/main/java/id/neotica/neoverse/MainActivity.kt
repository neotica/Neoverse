package id.neotica.neoverse

import android.os.Bundle
import android.util.Log
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hierarchy
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
                val bottomBarState = rememberSaveable { (mutableStateOf(true)) }

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry.fromRoute() //navBackStackEntry ?.destination?.route
                val currentDestination = navBackStackEntry?.destination

                LaunchedEffect(Unit) {
                    when(currentRoute) {
                        Screen.Splash.toString() -> bottomBarState.value = false
                        Screen.LoginScreen.toString() -> bottomBarState.value = false
                    }
                }
                val navItems = listOf(
                    NavigationItem(
                        title = "Home",
                        icon = Icons.Default.Home,
                        screen = RootScreen.HomeNav
                    ),
                    NavigationItem(
                        title = "Profile",
                        icon = Icons.Default.AccountCircle,
                        screen = RootScreen.AuthNav
                    ),
                    NavigationItem(
                        title = "Settings",
                        icon = Icons.Default.Settings,
                        screen = Screen.Setting
                    )
                )
                val excludedRoutes = listOf(
                    Screen.Splash.toString(),
                    Screen.LoginScreen,
                    Screen.RegisterScreen,
                ).map { it.toString().reformatScreen() }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        val currentHierarchy = currentDestination?.hierarchy

                        if (currentDestination?.route?.reformatScreen() !in excludedRoutes) {

                            NavigationBar {
                                navItems.map {
                                    NavigationBarItem(
                                        selected = currentHierarchy?.any { current ->
                                            current.route?.reformatScreen() == it.screen.toString().reformatScreen()
                                        } == true ,
                                        //currentDestination?.route?.reformatScreen() == it.screen.toString().reformatScreen(),
                                        onClick = {
                                            Log.d("neoActivity", "destination hierarchy: " + currentHierarchy.toString())
                                            Log.d("neoActivity", it.screen.toString().reformatScreen())
                                            Log.d("neoActivity", "findStart: ${navController.graph.findStartDestination()}")
                                            Log.d("neoActivity", "reformat current: ${currentRoute.toString().reformatScreen()}")
                                            Log.d("neoActivity", "destination: ${currentDestination?.route}")


                                            navController.navigate(it.screen.toString().reformatScreen()) {
                                                navController.graph.startDestinationRoute?.let { route ->
                                                    popUpTo(route) {
                                                        saveState = true
                                                    }
                                                }
                                                /*popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }*/
                                                launchSingleTop = true
                                                restoreState = true
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

    private fun NavBackStackEntry?.fromRoute(): Any {
        val route = this?.destination?.route?.substringBefore("?")?.substringBefore("/")?.substringAfterLast(".")
        return when (route) {
            RootScreen.HomeNav::class.simpleName -> RootScreen.HomeNav
            Screen.HomeScreen::class.simpleName -> Screen.HomeScreen
            RootScreen.AuthNav::class.simpleName -> RootScreen.AuthNav
            Screen.RegisterScreen::class.simpleName -> Screen.RegisterScreen
            Screen.LoginScreen::class.simpleName -> Screen.LoginScreen
            Screen.Setting::class.simpleName -> Screen.Setting
            Screen.Splash::class.simpleName -> Screen.Splash
            else -> ""
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

fun String.reformatScreen(): String {
    return this.substringBefore("@").replace("$", ".")
}
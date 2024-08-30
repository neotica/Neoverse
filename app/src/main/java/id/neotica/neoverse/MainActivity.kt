package id.neotica.neoverse

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
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

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                val navItems = listOf(
                    NavigationItem(
                        title = "Home",
                        icon = Icons.Default.Home,
                        screen = RootScreen.HomeNav
                    ),
                    NavigationItem(
                        title = "Friends",
                        icon = Icons.Default.AccountBox,
                        screen = RootScreen.FriendNav
                    ),
                    NavigationItem(
                        title = "Games",
                        icon = Icons.Default.Favorite,
                        screen = RootScreen.GameNav
                    ),
                    NavigationItem(
                        title = "Profile",
                        icon = Icons.Default.AccountCircle,
                        screen = RootScreen.AuthNav
                    )
                )
                val excludedRoutes = listOf(
                    Screen.Splash,
                    Screen.LoginScreen,
                    Screen.RegisterScreen,
                )

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        val currentHierarchy = currentDestination?.hierarchy
                        val includeRoute = excludedRoutes.none {
                            if (navBackStackEntry?.destination?.route == null) true else
                            navBackStackEntry?.destination?.hasRoute(it::class) == true
                        }

                        AnimatedVisibility (
                            visible = includeRoute,
                            enter = slideInVertically(initialOffsetY = { it }),
                            exit = slideOutVertically(targetOffsetY = { it }),
                        ) {

                            NavigationBar {
                                navItems.map {
                                    NavigationBarItem(
                                        selected = currentDestination?.hierarchy?.any { current ->
                                            current.hasRoute(it.screen::class)
                                        } == true,
                                        onClick = {
                                            Log.d("neoActivity", "destination hierarchy: " + currentHierarchy.toString())
                                            Log.d("neoActivity", "screen: " + it.screen.toString())
                                            Log.d("neoActivity", "findStart: ${navController.graph.findStartDestination()}")
                                            Log.d("neoActivity", "destination: ${currentDestination?.route}")


                                            navController.navigate(it.screen) {
                                                navController.graph.startDestinationRoute?.let { route ->
                                                    popUpTo(route) {
                                                        saveState = true
                                                    }
                                                }
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
}
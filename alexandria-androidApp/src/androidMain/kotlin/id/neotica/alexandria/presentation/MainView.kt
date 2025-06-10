package id.neotica.alexandria.presentation

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import id.neotica.alexandria.presentation.navigation.NavGraph
import id.neotica.alexandria.theme.NeoverseTheme
import id.neotica.ui.components.navigation.CustomNavBar
import id.neotica.routes.AlexandriaScreen
import id.neotica.routes.RootScreen
import id.neotica.routes.Screen
import id.neotica.routes.WorldScreen
import id.neotica.ui.navigation.NavigationItem
import id.neotica.ui.navigation.showExcept
import id.neotica.ui.shared.components.navigation.CustomNavigationSuiteScaffold
import id.neotica.ui.shared.components.navigation.NavigationSuiteScaffoldDefaults
import id.neotica.ui.shared.components.navigation.NavigationSuiteType
import id.neotica.ui.shared.state.AdaptiveState
import id.neotica.ui.theme.DarkBackground
import id.neotica.ui.theme.DarkPrimary
import org.koin.compose.KoinContext


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView() {
    KoinContext {
        NeoverseTheme {
            val navController = rememberNavController()

            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            val navItems = listOf(
                NavigationItem(
                    title = "Home",
                    icon = Icons.Default.Home,
                    screen = RootScreen.AlexandriaNav
                ),
                NavigationItem(
                    title = "Profile",
                    icon = Icons.Default.AccountCircle,
                    screen = Screen.ProfileScreen
                )
            )

            val screenSet = setOf(
                Screen.Splash,
                Screen.LoginScreen,
                Screen.RegisterScreen,
                Screen.VerifyEmailScreen,
                Screen.PrivacyPolicyScreen,
                Screen.TermsOfServiceScreen,
                Screen.DeleteAccountScreen,
                Screen.AboutScreen,
                Screen.AddChatScreen,
                Screen.UpdateAppScreen,
                Screen.CameraPreviewScreen,
                Screen.SearchUserScreen,
                Screen.FriendScreen,
                Screen.ResetPassword,
                Screen.Chat(),
                Screen.EditProfileScreen(),
            )

            val worldScreen = setOf(
                WorldScreen.TavernScreen,
                WorldScreen.MiningScreen,
                WorldScreen.LeaderBoardsScreen,
                WorldScreen.TerminalScreen,
                WorldScreen.RavenScreen,
                WorldScreen.RavenChatScreen,
                WorldScreen.ItemDetailScreen(),
            )

            val alexandriaSet = setOf(
                AlexandriaScreen.BookScreen(),
                AlexandriaScreen.UserLibraryScreen,
                AlexandriaScreen.EditBookScreen(),
                AlexandriaScreen.EditChapterScreen(),
                AlexandriaScreen.BookContentScreen(),
                AlexandriaScreen.AdminLibraryScreen,
                AlexandriaScreen.AdminScreen
            )

            val bottomBarExcludedRoutes = screenSet + worldScreen + alexandriaSet

            val includeRoute = navBackStackEntry.showExcept(bottomBarExcludedRoutes)

            Log.d("currentScreen", navBackStackEntry?.destination?.route.toString())

            val mapAdaptive = AdaptiveState.from(currentWindowAdaptiveInfo())
            val customNavSuiteType = when (mapAdaptive) {
                is AdaptiveState.Compact -> NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(
                    currentWindowAdaptiveInfo()
                )
                else -> NavigationSuiteType.NavigationRail
            }

            when(mapAdaptive) {
                is AdaptiveState.Compact -> {
                    Scaffold(
                        modifier = Modifier
                            .background(DarkBackground)
                            .fillMaxSize(),
                        bottomBar = {
                            AnimatedVisibility (
                                visible = includeRoute,
                                enter = slideInVertically(initialOffsetY = { it }),
                                exit = slideOutVertically(targetOffsetY = { it }),
                            ) {
                                CustomNavBar (
                                    modifier = Modifier
                                ) {
                                    navItems.map {
                                        NavigationBarItem(
                                            colors = NavigationBarItemColors(
                                                selectedIconColor = Color.White,
                                                selectedTextColor = Color.White,
                                                selectedIndicatorColor = DarkPrimary,
                                                unselectedIconColor = Color.White,
                                                unselectedTextColor = Color.White,
                                                disabledIconColor = Color.Gray,
                                                disabledTextColor = Color.Gray
                                            ),
                                            selected = currentDestination?.hierarchy?.any { current ->
                                                current.hasRoute(it.screen::class)
                                            } == true,
                                            onClick = {
                                                navController.navigate(it.screen) {
                                                    navController.graph.startDestinationId.let { route ->
                                                        popUpTo(AlexandriaScreen.HomeScreen) {
                                                            saveState = true
                                                            inclusive = false
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
                        NavGraph(
                            navController = navController,
                            paddingValues = PaddingValues(
                                top = innerPadding.calculateTopPadding(),
                                bottom = innerPadding.calculateBottomPadding() - WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()
                            ),
                        )
                    }
                }
                else -> {
                    CustomNavigationSuiteScaffold(
                        layoutType = customNavSuiteType,
                        navigationVisibility = includeRoute,
                        navigationSuiteItems = {
                            navItems.map { navMap ->
                                item(
                                    selected = currentDestination?.hierarchy?.any { current ->
                                        current.hasRoute(navMap.screen::class)
                                    } == true,
                                    onClick = {
                                        navController.navigate(navMap.screen) {
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
                                        Icon(imageVector = navMap.icon, contentDescription = navMap.title)
                                    },
                                    label = {
                                        Text(navMap.title)
                                    }
                                )
                            }
                        }
                    ) {
                        NavGraph(
                            navController = navController,
                            paddingValues = PaddingValues(),
                        )
                    }
                }
            }
        }
    }

}
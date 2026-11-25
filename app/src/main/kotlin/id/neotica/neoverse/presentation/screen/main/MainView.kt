package id.neotica.neoverse.presentation.screen.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
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
import co.touchlab.kermit.Logger
import dev.icerock.moko.resources.compose.painterResource
import id.neotica.neoverse.presentation.navigation.NavGraph
import id.neotica.res.MR
import id.neotica.routes.RootScreen
import id.neotica.routes.Screen
import id.neotica.ui.shared.components.navigation.CustomNavBar
import id.neotica.ui.shared.components.navigation.CustomNavigationSuiteScaffold
import id.neotica.ui.shared.components.navigation.NavigationSuiteScaffoldDefaults
import id.neotica.ui.shared.components.navigation.NavigationSuiteType
import id.neotica.ui.shared.navigation.NavigationItem
import id.neotica.ui.shared.navigation.showExcept
import id.neotica.ui.shared.state.AdaptiveState
import id.neotica.ui.shared.theme.DarkBackground
import id.neotica.ui.shared.theme.DarkPrimary
import id.neotica.ui.shared.theme.NeoverseTheme

@Composable
fun MainView() {
    NeoverseTheme {
        val navController = rememberNavController()

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        val navItems = listOf(
            NavigationItem(
                title = "Home",
                icon = painterResource(MR.images.ic_home),
                screen = RootScreen.HomeNav
            ),
            NavigationItem(
                title = "Profile",
                icon = painterResource(MR.images.ic_profile),
                screen = RootScreen.ProfileNav
            )
        )

        val screenSet = setOf(
            Screen.Splash,
            Screen.LoginScreen,
            Screen.RegisterScreen,
            Screen.VerifyEmailScreen(),
            Screen.Conversation(),
            Screen.PrivacyPolicyScreen,
            Screen.TermsOfServiceScreen,
            Screen.DeleteAccountScreen,
            Screen.AboutScreen,
            Screen.AddChatScreen,
            Screen.UpdateAppScreen,
            Screen.ResetPassword
        )

        val bottomBarExcludedRoutes = screenSet

        val includeRoute = navBackStackEntry.showExcept(bottomBarExcludedRoutes)
        Logger.d("currentRoute") { navBackStackEntry?.destination?.route.toString() }

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
                                                    popUpTo(Screen.HomeScreen) {
                                                        saveState = true
                                                        inclusive = false
                                                    }
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        icon = {
                                            Icon(painter = it.icon, contentDescription = it.title)
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
                                    Icon(painter = navMap.icon, contentDescription = navMap.title)
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
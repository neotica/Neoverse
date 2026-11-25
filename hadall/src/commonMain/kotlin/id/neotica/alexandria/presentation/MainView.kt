package id.neotica.alexandria.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.icerock.moko.resources.compose.painterResource
import id.neotica.alexandria.presentation.navigation.NavGraph
import id.neotica.res.MR
import id.neotica.routes.AlexandriaScreen
import id.neotica.routes.RootScreen
import id.neotica.routes.Screen
import id.neotica.routes.WorldScreen
import id.neotica.ui.shared.components.navigation.CustomNavBar
import id.neotica.ui.shared.components.navigation.CustomNavigationSuiteScaffold
import id.neotica.ui.shared.components.navigation.NavigationSuiteScaffoldDefaults
import id.neotica.ui.shared.components.navigation.NavigationSuiteType
import id.neotica.ui.shared.navigation.NavigationItem
import id.neotica.ui.shared.navigation.showExcept
import id.neotica.ui.shared.state.AdaptiveState
import id.neotica.ui.shared.theme.BlackTransparent40
import id.neotica.ui.shared.theme.DarkBackground
import id.neotica.ui.shared.theme.DarkPrimary
import id.neotica.ui.shared.theme.NegativePrimary

@Composable
expect fun NeoverseTheme(content: @Composable () -> Unit)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView() {
    MaterialTheme (
        colorScheme = darkColorScheme(
            primary = DarkPrimary,
            onPrimary = Color.White,
            secondary = Color.White,
            onSecondary = DarkPrimary,
            background = DarkBackground,
            onBackground = Color.White,
            tertiary = NegativePrimary,
            onTertiary = Color.White
        )
    )  {
        val navController = rememberNavController()

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        val navItems = listOf(
            NavigationItem(
                title = "Home",
                icon = painterResource(MR.images.ic_home),
                screen = RootScreen.AlexandriaNav
            ),
            NavigationItem(
                title = "Profile",
                icon = painterResource(MR.images.ic_profile),
                screen = Screen.ProfileScreen
            )
        )

        val screenSet = setOf(
            Screen.Splash,
            Screen.LoginScreen,
            Screen.RegisterScreen,
            Screen.VerifyEmailScreen(),
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
            Screen.InputProfileScreen
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
                    containerColor = BlackTransparent40,
                    modifier = Modifier
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
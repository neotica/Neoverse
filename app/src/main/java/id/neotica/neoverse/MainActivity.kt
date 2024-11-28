package id.neotica.neoverse

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.initialize
import id.neotica.neoverse.presentation.navigation.NavGraph
import id.neotica.ui.R
import id.neotica.ui.components.navigation.CustomNavBar
import id.neotica.ui.components.navigation.CustomNavigationSuiteScaffold
import id.neotica.ui.components.navigation.NavigationSuiteScaffoldDefaults
import id.neotica.ui.components.navigation.NavigationSuiteType
import id.neotica.ui.navigation.NavigationItem
import id.neotica.ui.navigation.RootScreen
import id.neotica.ui.navigation.Screen
import id.neotica.ui.state.AdaptiveState
import id.neotica.ui.theme.DarkBackground
import id.neotica.ui.theme.DarkPrimary
import id.neotica.ui.theme.NeoverseTheme

class MainActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()

        Firebase.initialize(this)
        Firebase.appCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance()
        )

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
                        title = "World",
                        icon = ImageVector.vectorResource(R.drawable.ic_world),
                        screen = RootScreen.WorldNav
                    ),
                    NavigationItem(
                        title = "Profile",
                        icon = Icons.Default.AccountCircle,
                        screen = RootScreen.AuthNav
                    )
                )
                val bottomBarExcludedRoutes = setOf(
                    Screen.Splash,
                    Screen.LoginScreen,
                    Screen.RegisterScreen,
                    Screen.VerifyEmailScreen,
                    Screen.PrivacyPolicyScreen,
                    Screen.TermsOfServiceScreen,
                    Screen.DeleteAccountScreen,
                    Screen.AboutScreen,
                    Screen.AddChatScreen
                )

                val includeRoute = bottomBarExcludedRoutes.none {
                    if (navBackStackEntry?.destination?.route == null) true else
                        navBackStackEntry?.destination?.hasRoute(it::class) == true
                }
                val specificRoutes = setOf(
                    "id.neotica.ui.navigation.Screen.Chat/{chatId}/{user2}?chatroomId={chatroomId}",
                    "id.neotica.ui.navigation.WorldScreen.ItemDetailScreen/{id}/{type}",
                    "id.neotica.ui.navigation.Screen.EditProfileScreen/{profPic}"
                )
                val currentRoute = navBackStackEntry?.destination?.route
                Log.d("currentScreen", navBackStackEntry?.destination?.route.toString())

                val includeRoute3 = includeRoute && currentRoute !in specificRoutes

                val mapAdaptive = AdaptiveState.from(currentWindowAdaptiveInfo())
                val customNavSuiteType = when (mapAdaptive) {
                    is AdaptiveState.Compact -> NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(currentWindowAdaptiveInfo())
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
                                    visible = includeRoute3,
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
                                                            popUpTo(route) {
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
                            navigationVisibility = includeRoute3,
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
}
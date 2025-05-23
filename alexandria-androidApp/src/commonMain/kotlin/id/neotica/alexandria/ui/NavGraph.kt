//package id.neotica.alexandria.ui
//
//import androidx.compose.animation.EnterTransition
//import androidx.compose.animation.ExitTransition
//import androidx.compose.animation.core.tween
//import androidx.compose.animation.fadeIn
//import androidx.compose.animation.fadeOut
//import androidx.compose.foundation.layout.PaddingValues
//import androidx.compose.runtime.Composable
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.NavHost
//import id.neotica.alexandria.navigation.alexandriaNavGraph
//import id.neotica.ui.navigation.RootScreen
//import id.neotica.ui.state.AppVariant
//import org.koin.compose.getKoin
//
//@Composable
//fun NavGraph(
//    navController: NavHostController,
//    paddingValues: PaddingValues,
//) {
//    val appVariant: AppVariant = getKoin().get()
//
//    NavHost(
//        navController,
//        startDestination = RootScreen.AlexandriaNav,
//        enterTransition = { fadeIn(tween(100)) },
//        popEnterTransition = { EnterTransition.None },
//        exitTransition = { fadeOut(tween(100)) },
//        popExitTransition = { ExitTransition.None }
//    ) {
////        composable<Screen.Splash> { SplashView(navController = navController) }
////        composable<Screen.Setting> { SettingView(paddingValues) }
////        composable<Screen.DumpScreen> { DumpView(navController, paddingValues = paddingValues) }
//
//        alexandriaNavGraph(navController, appVariant)
//        profileNavGraph(navController, paddingValues, appVariant)
//
////        profileNavGraph(navController, paddingValues)
////        worldNavGraph(navController, paddingValues)
////        friendNavGraph(navController, paddingValues)
//    }
//}
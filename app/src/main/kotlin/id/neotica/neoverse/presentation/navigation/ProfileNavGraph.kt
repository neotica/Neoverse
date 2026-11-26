package id.neotica.neoverse.presentation.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import id.neotica.auth.authNavGraph
import id.neotica.profile.presentation.ProfileRoute
import id.neotica.profile.presentation.about.AboutRoute
import id.neotica.profile.presentation.accountsettings.AccountSettingRoute
import id.neotica.profile.presentation.deleteaccount.DeleteAccountRoute
import id.neotica.profile.presentation.privacypolicy.PrivacyPolicyRoute
import id.neotica.profile.presentation.resetpassword.ResetPasswordRoute
import id.neotica.profile.presentation.termsofservice.TermsOfServiceRoute
import id.neotica.routes.RootScreen
import id.neotica.routes.Screen
import id.neotica.state.AppVariant

fun NavGraphBuilder.profileNavGraph(
    navController: NavController,
    paddingValues: PaddingValues,
    appVariant: AppVariant,
) {
    navigation<RootScreen.ProfileNav>(
        startDestination = Screen.ProfileScreen,
    ) {
        authNavGraph(navController, paddingValues, appVariant)

        composable<Screen.ProfileScreen> {
            ProfileRoute(
                navController,
                paddingValues = paddingValues,
                appVariant = appVariant,
            )
        }
        composable<Screen.PrivacyPolicyScreen>(
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
        ) {
            PrivacyPolicyRoute(navController)
        }
        composable<Screen.TermsOfServiceScreen>(
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
        ) {
            TermsOfServiceRoute(navController)
        }
        composable<Screen.AccountSettingScreen>(
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
        ) {
            AccountSettingRoute(navController, paddingValues)
        }
        composable<Screen.DeleteAccountScreen>(
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
        ) {
            DeleteAccountRoute(navController, paddingValues)
        }
        composable<Screen.AboutScreen>(
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
        ) {
            AboutRoute(navController)
        }
        composable<Screen.ResetPassword>(
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
        ) {
            ResetPasswordRoute(navController)
        }
    }
}

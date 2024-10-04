package id.neotica.neoverse.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import id.neotica.neoverse.presentation.screen.friend.profile.FriendProfileView
import id.neotica.neoverse.presentation.screen.friend.FriendView
import id.neotica.neoverse.presentation.screen.friend.search.SearchUser
import id.neotica.ui.navigation.RootScreen
import id.neotica.ui.navigation.Screen

fun NavGraphBuilder.friendNavGraph(
    navController: NavController,
    paddingValues: PaddingValues
) {
    navigation<RootScreen.FriendNav>(startDestination = Screen.FriendScreen) {
        composable<Screen.FriendScreen> {
            FriendView(navController, paddingValues) }
        composable<Screen.SearchUserScreen> {
            SearchUser(navController = navController, paddingValues = paddingValues)
        }
        composable<Screen.FriendProfileScreen> {
            val args = it.toRoute<Screen.FriendProfileScreen>()
            FriendProfileView(
                navController,
                paddingValues = paddingValues,
                userId = args.userId
            )
        }
    }
}
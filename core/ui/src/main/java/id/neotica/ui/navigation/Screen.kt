package id.neotica.ui.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable
    data object Splash: Screen()

    // Home Navigation
    @Serializable
    data object HomeScreen: Screen()

    @Serializable
    data class Chat(
        val chatId: String,
        val user2: String,
        val chatroomId: String ?= ""
        )
    @Serializable
    data object Setting: Screen()
    @Serializable
    data object ChatFromHome: Screen()
    @Serializable
    data object DumpScreen: Screen()

    // Auth Navigation
    @Serializable
    data object LoginScreen: Screen()
    @Serializable
    data object RegisterScreen: Screen()
    @Serializable
    data object ProfileScreen: Screen()
    @Serializable
    data object AccountSettingScreen: Screen()
    @Serializable
    data class EditProfileScreen(val profPic: String)
    @Serializable
    data object VerifyEmailScreen: Screen()
    @Serializable
    data object PrivacyPolicyScreen: Screen()
    @Serializable
    data object TermsOfServiceScreen: Screen()
    @Serializable
    data object DeleteAccountScreen: Screen()

    // World Navigation
    @Serializable
    data object WorldScreen: Screen()
    @Serializable
    data object GameScreen: Screen()
    @Serializable
    data object KanyeScreen: Screen()
    @Serializable
    data object MarketScreen: Screen()
    @Serializable
    data class ItemDetailScreen(val id: String)

    //Friend Navigation
    @Serializable
    data object FriendScreen: Screen()
    @Serializable
    data object SearchUserScreen: Screen()
    @Serializable
    data class FriendProfileScreen(
        val userId: String
    )
}

@Serializable
sealed class RootScreen {
    @Serializable
    object AuthNav
    @Serializable
    object HomeNav
    @Serializable
    object WorldNav
    @Serializable
    object FriendNav
}

data class NavigationItem(
    val title: String,
    val icon: ImageVector,
    val screen: Any
)
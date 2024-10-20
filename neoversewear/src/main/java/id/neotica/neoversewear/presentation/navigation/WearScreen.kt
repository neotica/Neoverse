package id.neotica.neoversewear.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class WearScreen {
    @Serializable
    data object HomeNavScreen: WearScreen()
    @Serializable
    data object HomeScreen: WearScreen()

    // Auth
    @Serializable
    data object LoginScreen: WearScreen()

    @Serializable
    data object ProfileScreen: WearScreen()

    //Chat
    @Serializable
    data class ChatScreen(
        val chatId: String,
        val user2: String,
        val chatroomId: String? = ""
    )
}
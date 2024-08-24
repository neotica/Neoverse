package id.neotica.neoverse.data.model

data class User(
    var userId: String? = "",
    val username: String? = "",
    val userEmail: String? = "",
    val status: String? = "default",
    val imageUrl: String ?= "https://www.pngarts.com/files/6/User-Avatar-in-Suit-PNG.png"
)
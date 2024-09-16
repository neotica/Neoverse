package id.neotica.domain.model

data class UserData(
    val id: String = "",
    val friends: ParentNeo<Friends>,
    val games: Games
)

data class Friends(
    val id: String = "",
    val name: String = "",
    val imageUrl: String = ""
)

data class Games(
    var aura: Int = 0
)
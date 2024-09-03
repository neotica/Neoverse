package id.neotica.neoverse.data.model

data class UserData(
    val id: String = "",
    val friends: ParentNeo<Friends>,
    val games: Games
)

data class Friends(
    val id: String = "",
    val name: String = ""
)

data class Games(
    var aura: Int = 0
)
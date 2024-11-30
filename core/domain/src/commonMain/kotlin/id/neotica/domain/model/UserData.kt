package id.neotica.domain.model

data class UserData(
    val userId: String = "",
    val friends: Friends = Friends(),
    val world: World = World()
)

data class Friends(
    val id: String = "",
    val name: String = "",
    val imageUrl: String = ""
)

data class World(
    val username: String = "",
    var aura: Long = 0
)
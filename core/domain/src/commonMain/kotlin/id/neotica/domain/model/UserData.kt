package id.neotica.domain.model

import id.neotica.domain.model.market.Item

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
    var aura: Long = 0,
    val items: Item = Item()
)

data class UserDataFirebase(
    val userId: String = "",
    val friends: Friends = Friends(),
    val world: WorldFirebase = WorldFirebase()
)

data class WorldFirebase(
    val username: String = "",
    var aura: String = "",
    val items: Item = Item()
)
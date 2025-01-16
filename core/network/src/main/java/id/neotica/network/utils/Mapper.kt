package id.neotica.network.utils

import id.neotica.domain.model.UserData
import id.neotica.domain.model.UserDataFirebase
import id.neotica.domain.model.World
import id.neotica.domain.model.chat.ParentNeo

fun userDataToLong(userData: ParentNeo<UserDataFirebase>): ParentNeo<UserData> {
    return ParentNeo(
        key = userData.key,
        value = UserData(
            userId = userData.value.userId,
            friends = userData.value.friends,
            world = World(
                username = userData.value.world.username,
                aura = LongCryptoUtil.decrypt(userData.value.world.aura),
                items = userData.value.world.items
            )
        )
    )
}
package id.neotica.neoverse.domain.utils

import id.neotica.neoverse.data.local.FriendEntity
import id.neotica.neoverse.data.model.User

object Mapper {
    fun friendsResponseToEntity(input: List<User>): List<FriendEntity> {
        val userList = ArrayList<FriendEntity>()
        input.map {
            val user = FriendEntity(
                id = it.userId,
                username = it.username,
                email = it.userEmail,
                status = it.status,
                imageUrl = it.imageUrl
            )
            userList.add(user)
        }
        return userList
    }

    fun friendResponseToEntity(input: User) = FriendEntity(
        id = input.userId,
        username = input.username,
        email = input.userEmail,
        status = input.status,
        imageUrl = input.imageUrl
    )
}
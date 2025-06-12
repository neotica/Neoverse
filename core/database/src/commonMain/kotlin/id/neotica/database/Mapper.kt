package id.neotica.database

import id.neotica.database.entity.FriendEntity
import id.neotica.database.entity.CurrentUserEntity
import id.neotica.domain.model.User

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

    fun userResponseToEntity(input: User) = CurrentUserEntity(
        id = input.userId,
        username = input.username,
        email = input.userEmail,
        status = input.status,
        imageUrl = input.imageUrl
    )

    fun FriendEntity.toUser(): User {
        return User(
            userId = id,
            username = username,
            userEmail = email,
            status = status,
            imageUrl = imageUrl
        )
    }

    fun CurrentUserEntity.toUser(): User {
        return User(
            userId = id,
            username = username,
            userEmail = email,
            status = status,
            imageUrl = imageUrl
        )
    }

}
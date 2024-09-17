package id.neotica.neoverse.data.local

import id.neotica.neoverse.data.local.entity.FriendEntity
import id.neotica.neoverse.data.local.entity.CurrentUserEntity
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

    fun friendEntityToResponse(input: FriendEntity) = User(
        userId = input.id,
        username = input.username,
        userEmail = input.email,
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

    fun userResponseToEntity(input: List<User>): List<CurrentUserEntity> {
        val userList = ArrayList<CurrentUserEntity>()
        input.map {
            val user = CurrentUserEntity(
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

    fun CurrentUserEntity.toUser(): User {
        return User(
            userId = id,
            username = username,
            userEmail = email,
            status = status,
            imageUrl = imageUrl
        )
    }

    /*fun userEntityToResponse(input: CurrentUserEntity) = User(
        userId = input.id,
        username = input.username,
        userEmail = input.email,
        status = input.status,
        imageUrl = input.imageUrl
    )*/
}
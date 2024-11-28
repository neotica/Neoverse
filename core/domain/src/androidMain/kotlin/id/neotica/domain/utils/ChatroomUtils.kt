package id.neotica.domain.utils

fun filteredRoom(current: String, user1: String, user2: String): Boolean {
    return userCheck(current, user1, true) ||
            userCheck(current, user2, true)
}

fun returnFriendName(current: String, user2: String, user3: String = ""): String {
    return if (userCheck(current, user2)) user2 else user3
}

fun userCheck(current: String, user2: String, equal: Boolean = false): Boolean {
    return if (!equal) current != user2 else current == user2
}
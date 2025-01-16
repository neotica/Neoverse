package id.neotica.neoverse

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
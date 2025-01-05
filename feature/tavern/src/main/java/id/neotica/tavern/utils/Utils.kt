package id.neotica.tavern.utils

import java.security.SecureRandom
import kotlin.random.asKotlinRandom

fun generateTranspose(random: SecureRandom): String {
    val randomValue = random.asKotlinRandom(). nextInt(1,80)
    return when (randomValue) {
        in 1..15 -> "7\uFE0F"
        in 21..40 -> "\uD83D\uDE0A"
        in 41..60 -> "⭐\uFE0F"
        else -> "\uD83E\uDD88"
    }
}
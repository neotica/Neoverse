package id.neotica.domain.url

import id.neotica.neoverse.utils.baseUrl

object AlexandriaUrl {
    const val HOME = "$baseUrl/books"
    const val CHAPTER = "$HOME/chapters"
    const val CONTENT = "$CHAPTER/contents"
}
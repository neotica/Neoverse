package id.neotica.alexandria.alexandria

import id.neotica.domain.model.alexandria.Book
import id.neotica.domain.model.alexandria.Chapter

class ChapterDummy {
    val bookResponse = Book(
        id = "2ea3741e-4b0d-42ad-8b5b-e4be983611fc",
        title = "This is the end",
        author = "tarakh",
        language = "EN",
        isPublished = true,
        createdAt = 1745407419573,
    )
    val chapterResponse = listOf<Chapter>(
        Chapter(
            id = 1,
            title = "First Chapter",
            bookId = "2ea3741e-4b0d-42ad-8b5b-e4be983611fc",
            createdAt = 1745407439367,
            modifiedAt = 1745407439367,
        ),
        Chapter(
            id = 2,
            title = "Second Chapter",
            bookId = "2ea3741e-4b0d-42ad-8b5b-e4be983611fc",
            createdAt = 1745407602240,
            modifiedAt = 1745407602240,
            image = null
        )
    )
    val chaptersResponseJson = """
            [
                {
                    "id": 1,
                    "title": "First Chapter",
                    "bookId": "2ea3741e-4b0d-42ad-8b5b-e4be983611fc",
                    "createdAt": 1745407439367,
                    "modifiedAt": 1745407439367
                },
                {
                    "id": 2,
                    "title": "Second Chapter",
                    "bookId": "2ea3741e-4b0d-42ad-8b5b-e4be983611fc",
                    "createdAt": 1745407602240,
                    "modifiedAt": 1745407602240
                }
            ]
        """.trimIndent()
}
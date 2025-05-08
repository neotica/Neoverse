package id.neotica.network.alexandria

import id.neotica.domain.model.alexandria.Book
import id.neotica.domain.model.alexandria.book.BookRequest

class BookDummy {
    val bookResponse = Book(
        id = "2ea3741e-4b0d-42ad-8b5b-e4be983611fc",
        title = "Frankenstein",
        author = "tarakh",
        language = "EN",
        isPublished = true,
        createdAt = 1745407419573,
    )
    val paginatedBookResponseJson = """
            {
                    "content": [
                        {
                            "uuid": "2ea3741e-4b0d-42ad-8b5b-e4be983611fc",
                            "title": "Frankenstein",
                            "language": "EN",
                            "isPublished": true,
                            "author": "tarakh",
                            "createdAt": 1745407419573
                        }
                    ],
                    "currentPage": 1,
                    "totalPages": 1,
                    "pageSize": 10,
                    "hasNextPage": false,
                    "hasPreviousPage": false
            }
        """.trimIndent()

    val emptyBookResponseJson = """
            {
                    "content": [],
                    "currentPage": 0,
                    "totalPages": 0,
                    "pageSize": 10,
                    "hasNextPage": false,
                    "hasPreviousPage": false
            }
        """.trimIndent()

    val booksByAuthorResponse = listOf<Book>(bookResponse)

    val booksByAuthorResponseJson = """
            [
                {
                    "uuid": "2ea3741e-4b0d-42ad-8b5b-e4be983611fc",
                    "title": "Frankenstein",
                    "language": "EN",
                    "isPublished": true,
                    "author": "tarakh",
                    "createdAt": 1745407419573
                }
            ]
        """.trimIndent()

    val addBookRequest = BookRequest(
        title = "Frankenstein",
        author = "tarakh",
        language = "EN",
        isPublished = true
    )

    val addBookResponseJson = """
            {
                "uuid": "${bookResponse.id}",
                "title": "${bookResponse.title}",
                "author": "${bookResponse.author}",
                "language": "${bookResponse.language}",
                "summary": ${bookResponse.summary},
                "isPublished": ${bookResponse.isPublished},
                "createdAt": ${bookResponse.createdAt}
            }
        """.trimIndent()

}
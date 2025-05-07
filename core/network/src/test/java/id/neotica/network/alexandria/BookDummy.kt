package id.neotica.network.alexandria

import id.neotica.domain.model.alexandria.Book

class BookDummy {
    val mockBook = Book(
        id = "2ea3741e-4b0d-42ad-8b5b-e4be983611fc",
        title = "Frankenstein",
        author = "tarakh",
        language = "EN",
        isPublished = true,
        createdAt = 1745407419573,
    )
    val responseSuccess = """
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

    val responseEmpty = """
            {
                    "content": [],
                    "currentPage": 0,
                    "totalPages": 0,
                    "pageSize": 10,
                    "hasNextPage": false,
                    "hasPreviousPage": false
            }
        """.trimIndent()
}
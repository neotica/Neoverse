package id.neotica.alexandria.admin

import id.neotica.domain.model.admin.WarningResponse

class AdminDummy {
    val warningResponse = WarningResponse(
        id = "4cfb8a51-ba8a-4825-a7b6-7d352663d1ed",
        bookId = "93c835b6-6545-4570-a3e2-b3b32af6b82d",
        bookTitle = "The Bloody Ages 1",
        isAppealed = false,
        isApproved = false,
        message = "You have psoted too much",
        version = 1,
        createdAt = 1745158041796
    )
    val warningResponseJson = """
            {
                "content": [
                    {
                        "id": "4cfb8a51-ba8a-4825-a7b6-7d352663d1ed",
                        "bookId": "93c835b6-6545-4570-a3e2-b3b32af6b82d",
                        "bookTitle": "The Bloody Ages 1",
                        "isAppealed": false,
                        "isApproved": false,
                        "message": "You have psoted too much",
                        "version": 1,
                        "createdAt": 1745158041796
                    }
                ],
                "currentPage": 1,
                "totalPages": 1,
                "pageSize": 10,
                "hasNextPage": false,
                "hasPreviousPage": false
            }
        """.trimIndent()

    val updatedWarningResponse = WarningResponse(
        id = "8bab7937-c02c-4cd2-a753-e9fcaca52754",
        bookId = "93c835b6-6545-4570-a3e2-b3b32af6b82d",
        bookTitle = "The Bloody Ages 1",
        isAppealed = true,
        isApproved = false,
        message = "You have psoted too much",
        version = 1,
        createdAt = 1745234211269
    )

    val updateWarningRequestJson = """
            {
                "isAppealed":true,
                "isApproved":false
            }
        """.trimIndent()

    val updatedWarningJson = """
        {
            "id": "8bab7937-c02c-4cd2-a753-e9fcaca52754",
            "bookId": "93c835b6-6545-4570-a3e2-b3b32af6b82d",
            "bookTitle": "The Bloody Ages 1",
            "isAppealed": true,
            "isApproved": false,
            "message": "You have psoted too much",
            "version": 1,
            "createdAt": 1745234211269
        }
    """.trimIndent()
}
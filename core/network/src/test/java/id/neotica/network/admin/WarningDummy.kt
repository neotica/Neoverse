package id.neotica.network.admin

import id.neotica.domain.model.admin.WarningRequest
import id.neotica.domain.model.admin.WarningResponse

class WarningDummy {
    val warningResponse = listOf<WarningResponse>(
        WarningResponse(
            id = "8bab7937-c02c-4cd2-a753-e9fcaca52754",
            bookId = "93c835b6-6545-4570-a3e2-b3b32af6b82d",
            bookTitle = "The Bloody Ages 1",
            isAppealed = false,
            isApproved = false,
            message = "You have posted too much",
            version = 1,
            createdAt = 1745234211269
        )
    )
    val warningResponseJson = """
            {
                "content": [
                    {
                        "id": "8bab7937-c02c-4cd2-a753-e9fcaca52754",
                        "bookId": "93c835b6-6545-4570-a3e2-b3b32af6b82d",
                        "bookTitle": "The Bloody Ages 1",
                        "isAppealed": false,
                        "isApproved": false,
                        "message": "You have posted too much",
                        "version": 1,
                        "createdAt": 1745234211269
                    }
                ],
                "currentPage": 1,
                "totalPages": 1,
                "pageSize": 10,
                "hasNextPage": false,
                "hasPreviousPage": false
            }
        """.trimIndent()

    val newWarningRequest = WarningRequest(
        bookId = "93c835b6-6545-4570-a3e2-b3b32af6b82d",
        isAppealed = false,
        isApproved = false,
        version = 1,
        message = "You have posted too much"
    )
    val postWarningRequestJson = """
        {
            "bookId": "93c835b6-6545-4570-a3e2-b3b32af6b82d",
            "isAppealed": false,
            "isApproved": false,
            "version": 1,
            "message": "You have posted too much"
        }
    """.trimIndent()
    val newWarningResponse = WarningResponse(
        id = "4b8c9920-194a-47ea-a974-68e5a918fff1",
        bookId = "93c835b6-6545-4570-a3e2-b3b32af6b82d",
        bookTitle = "The Bloody Ages 1",
        isAppealed = false,
        isApproved = false,
        message = "You have posted too much",
        version = 1,
        createdAt = 1747254046336
    )
    val postWarningResponseJson = """
        {
            "id": "4b8c9920-194a-47ea-a974-68e5a918fff1",
            "bookId": "93c835b6-6545-4570-a3e2-b3b32af6b82d",
            "bookTitle": "The Bloody Ages 1",
            "isAppealed": false,
            "isApproved": false,
            "message": "You have posted too much",
            "version": 1,
            "createdAt": 1747254046336
        }
    """.trimIndent()
    val postWarningAlreadyExist = "Warning for this book already exists"

    val putWarningRequest = WarningRequest(
        message = "Great",
        isAppealed = true,
        isApproved = false,
        version = 2
    )
    val putWarningRequestJson = """
        {
            "message": "Great.",
            "isAppealed": true,
            "isApproved": false,
            "version": 2
        }
    """.trimIndent()
    val putWarningResponse = WarningResponse(
        id = "8bab7937-c02c-4cd2-a753-e9fcaca52754",
        bookId = "93c835b6-6545-4570-a3e2-b3b32af6b82d",
        bookTitle = "The Bloody Ages 1",
        isAppealed = true,
        isApproved = false,
        message = "Great.",
        version = 2,
        createdAt = 1745234211269
    )
    val putWarningResponseJson = """
        {
            "id": "8bab7937-c02c-4cd2-a753-e9fcaca52754",
            "bookId": "93c835b6-6545-4570-a3e2-b3b32af6b82d",
            "bookTitle": "The Bloody Ages 1",
            "isAppealed": true,
            "isApproved": false,
            "message": "Great.",
            "version": 2,
            "createdAt": 1745234211269
        }
    """.trimIndent()

    val deleteWarningResponseNotFound = "Warning not found"
    val deleteWarningResponseSucceeded = "Warning deleted successfully"
}
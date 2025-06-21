package id.neotica.network.micro

import id.neotica.domain.model.chat.notification.SendMessageDto
import retrofit2.http.Body
import retrofit2.http.POST

interface NetworkApiService {
    @POST("/notification/send")
    suspend fun sendMessage(
        @Body body: SendMessageDto
    )
}
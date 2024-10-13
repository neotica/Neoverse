package id.neotica.retrofit

import id.neotica.domain.model.kanye.KanyeQuotes
import retrofit2.http.GET

interface ApiService {
    @GET("/")
    suspend fun getQuote(): KanyeQuotes
}
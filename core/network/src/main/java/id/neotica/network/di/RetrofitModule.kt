package id.neotica.network.di

import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val retrofitModule = module {
    val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()
    single {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://18.141.159.232:8080")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        retrofit.create(NetworkApiService::class.java)
    }
}
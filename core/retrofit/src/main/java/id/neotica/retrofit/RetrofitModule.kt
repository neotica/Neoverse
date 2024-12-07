package id.neotica.retrofit

import id.neotica.domain.remote.KanyeRepository
import okhttp3.OkHttpClient
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val retrofitKanyeModule = module {
    val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()
    single {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.kanye.rest")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        retrofit.create(ApiService::class.java)
    }
    singleOf(::KanyeRepositoryImpl) bind KanyeRepository::class
}
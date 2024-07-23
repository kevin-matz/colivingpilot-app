package de.fhe.ai.colivingpilot.network

import de.fhe.ai.colivingpilot.core.KeyValueStore
import de.fhe.ai.colivingpilot.network.auth.AuthInterceptor
import de.fhe.ai.colivingpilot.network.service.BackendService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "http://tool.colivingpilot.de:20013"
    private lateinit var okHttpClient: OkHttpClient

    fun initialize(keyValueStore: KeyValueStore) {
        okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(keyValueStore))
            .build()
    }

    val instance: BackendService by lazy {
        if (!::okHttpClient.isInitialized) {
            throw IllegalStateException("OkHttpClient has not been initialized")
        }

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(BackendService::class.java)
    }

}
package de.fhe.ai.colivingpilot.network.auth

import de.fhe.ai.colivingpilot.core.KeyValueStore
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Invocation

class AuthInterceptor(private val keyValueStore: KeyValueStore) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val newRequestBuilder = request.newBuilder()

        val requestNeedsAuth = request.tag(Invocation::class.java)?.method()?.getAnnotation(
            AuthRequired::class.java) != null
        if (requestNeedsAuth) {
            val token = keyValueStore.readString("jwt")
            newRequestBuilder.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(newRequestBuilder.build())
    }
}
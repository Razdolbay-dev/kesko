package com.companykesko.keskoapp.data

import okhttp3.Interceptor
import okhttp3.Response
import kotlinx.coroutines.runBlocking

class AuthInterceptor(
    private val tokenProvider: () -> String?
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        // Не добавляем токен к самому логину
        if (original.url.encodedPath.contains("/api/users/login")) {
            return chain.proceed(original)
        }

        val token = tokenProvider()

        val request = if (token.isNullOrEmpty()) {
            original
        } else {
            original.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        }

        return chain.proceed(request)
    }
}
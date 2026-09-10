package com.companykesko.keskoapp.data

import android.content.Context
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    private const val BASE_URL = "http://inpulse.pit.su/"

    // In-memory кэш токена (чтобы Interceptor не ходил в DataStore на каждом запросе)
    @Volatile
    private var cachedToken: String? = null

    private lateinit var appContext: Context

    // Инициализация — вызвать один раз при старте приложения (в Application или MainActivity)
    fun init(context: Context) {
        appContext = context.applicationContext
        // Подгружаем токен из DataStore в память
        cachedToken = runBlocking { TokenStore.get(appContext) }
    }

    fun setToken(token: String?) {
        cachedToken = token
    }

    fun getToken(): String? = cachedToken

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val authInterceptor = AuthInterceptor { cachedToken }

    private val okHttp = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addInterceptor(authInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val service: ApiService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttp)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)
}
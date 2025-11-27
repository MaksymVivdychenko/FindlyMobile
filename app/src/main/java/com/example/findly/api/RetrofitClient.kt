package com.example.findly.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // ВАЖЛИВО: Для емулятора localhost це 10.0.2.2
    // Переконайся, що твій сервер працює по HTTP, або налаштуй HTTPS сертифікати (для тесту краще HTTP)
    private const val BASE_URL = "http://10.0.2.2:5132/api/" // Вкажи свій порт замість 50xx

    // Тут ми додамо перехоплювач (Interceptor), щоб додавати токен до запитів пізніше
    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
            val token = com.example.findly.utils.TokenManager.getToken()
            if (token != null) {
                requestBuilder.header("Authorization", "Bearer $token")
            }

            val request = requestBuilder.build()
            chain.proceed(request)
        }
        .build()

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
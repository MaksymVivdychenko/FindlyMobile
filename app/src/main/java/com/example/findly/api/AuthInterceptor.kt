package com.example.findly.api

import com.example.findly.utils.SessionManager
import com.example.findly.utils.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor{
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
        TokenManager.getToken()?.let {
            request.addHeader("Authorization", "Bearer $it")
        }
        val response = chain.proceed(request.build())
        if(response.code == 401)
        {
            SessionManager.logout()
        }

        return response
    }

}
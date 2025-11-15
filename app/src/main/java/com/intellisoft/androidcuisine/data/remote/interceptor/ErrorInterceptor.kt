package com.intellisoft.androidcuisine.data.remote.interceptor

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class ErrorInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        when (response.code) {
            400 -> Log.e("ErrorInterceptor", "Bad Request: ${request.url}")
            403 -> Log.e("ErrorInterceptor", "Forbidden: ${request.url}")
            404 -> Log.e("ErrorInterceptor", "Not Found: ${request.url}")
            500 -> Log.e("ErrorInterceptor", "Server Error: ${request.url}")
        }

        return response
    }
}
package com.intellisoft.androidcuisine.data.remote.interceptor

import android.content.Context
import android.content.Intent
import android.util.Log
import com.intellisoft.androidcuisine.util.SessionManager
import com.intellisoft.androidcuisine.views.auth.LoginActivity
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val context: Context) : Interceptor {

    private val sessionManager by lazy { SessionManager.getInstance(context) }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val bearerToken = sessionManager.getBearerToken()

        val newRequest = if (bearerToken != null && shouldAddAuth(originalRequest.url.encodedPath)) {
            originalRequest.newBuilder()
                .addHeader("Authorization", bearerToken)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .build()
        } else {
            originalRequest
        }

        val response = chain.proceed(newRequest)

        // Token expirado - redirigir al login
        if (response.code == 401 && bearerToken != null) {
            Log.w("AuthInterceptor", "Token expirado (401)")
            sessionManager.clearSession()

            // Redirigir al login
            val intent = Intent(context, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            context.startActivity(intent)
        }

        return response
    }

    private fun shouldAddAuth(path: String): Boolean {
        return !path.contains("/auth/login", ignoreCase = true)
    }
}
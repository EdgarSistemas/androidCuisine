package com.intellisoft.androidcuisine.data.remote.interceptor

import android.content.Context
import android.util.Log
import com.intellisoft.androidcuisine.data.managers.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val context: Context) : Interceptor {

    private val sessionManager by lazy { SessionManager.getInstance(context) }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Obtener token Bearer de la sesión
        val bearerToken = sessionManager.getBearerToken()

        val newRequest = if (bearerToken != null && shouldAddAuth(originalRequest.url.encodedPath)) {
            Log.d("AuthInterceptor", "🔐 Agregando Bearer token a: ${originalRequest.url}")
            Log.d("AuthInterceptor", "🔑 Token: ${bearerToken.take(20)}...")

            originalRequest.newBuilder()
                .addHeader("Authorization", bearerToken)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .build()
        } else {
            Log.d("AuthInterceptor", "🔓 Sin autenticación para: ${originalRequest.url}")
            originalRequest
        }

        val response = chain.proceed(newRequest)

        // Verificar si el token expiró
        if (response.code == 401 && bearerToken != null) {
            Log.w("AuthInterceptor", "⚠️ Token expirado o inválido (401)")
            // Aquí podrías implementar refresh token o limpiar sesión
            sessionManager.clearSession()
        }

        return response
    }

    /**
     * Determina si se debe agregar autenticación a la petición
     */
    private fun shouldAddAuth(path: String): Boolean {
        // No agregar auth a endpoint de login
        return !path.contains("/auth/login", ignoreCase = true)
    }
}

class ErrorInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        // Aquí puedes manejar errores comunes
        return response
    }
}

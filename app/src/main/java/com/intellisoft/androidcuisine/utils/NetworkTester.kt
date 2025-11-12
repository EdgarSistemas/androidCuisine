package com.intellisoft.androidcuisine.utils

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

object NetworkTester {

    private fun getApiBaseUrl(): String {
        // Para dispositivo físico, usa IP real de la PC
        // Para emulador, usa 10.0.2.2
        return "http://10.74.241.162:5000/api"
    }

    suspend fun testApiConnection(email: String, password: String, fcmToken: String): ApiResult {
        return withContext(Dispatchers.IO) {
            try {

                val baseUrl = getApiBaseUrl()
                val fullUrl = "$baseUrl/auth/login"
                Log.d("NetworkTester", "URL completa: $fullUrl")

                val client = OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)  // Aumenté timeout
                    .readTimeout(10, TimeUnit.SECONDS)
                    .addInterceptor { chain ->
                        val request = chain.request()
                        Log.d("NetworkTester", "Enviando request a: ${request.url}")
                        Log.d("NetworkTester", "Headers: ${request.headers}")
                        Log.d("NetworkTester", "Method: ${request.method}")

                        val response = chain.proceed(request)
                        Log.d("NetworkTester", "Response code: ${response.code}")
                        Log.d("NetworkTester", "Response message: ${response.message}")
                        response
                    }
                    .build()

                val json = """
                    {
                        "email": "$email",
                        "password": "$password", 
                        "plataforma": "android",
                        "push_token": "$fcmToken"
                    }
                """.trimIndent()

                Log.d("NetworkTester", "📋 Payload JSON: $json")

                val mediaType = "application/json; charset=utf-8".toMediaType()
                val requestBody = json.toRequestBody(mediaType)

                val request = Request.Builder()
                    .url(fullUrl)
                    .post(requestBody)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build()

                Log.d("NetworkTester", "🚀 Ejecutando petición a: $fullUrl")

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                Log.d("NetworkTester", "📨 Respuesta recibida:")
                Log.d("NetworkTester", "   - Código: ${response.code}")
                Log.d("NetworkTester", "   - Mensaje: ${response.message}")
                Log.d("NetworkTester", "   - Body: $responseBody")

                if (response.isSuccessful) {
                    ApiResult.Success(responseBody ?: "")
                } else {
                    ApiResult.Error("HTTP ${response.code}: $responseBody")
                }

            } catch (e: java.net.ConnectException) {
                Log.e("NetworkTester", "❌ Error de conexión: ${e.message}")
                ApiResult.Error("No se puede conectar al servidor: ${e.message}")
            } catch (e: Exception) {
                Log.e("NetworkTester", "❌ Error inesperado: ${e.message}")
                e.printStackTrace()
                ApiResult.Error("Error: ${e.message}")
            }
        }
    }
}

sealed class ApiResult {
    data class Success(val data: String) : ApiResult()
    data class Error(val message: String) : ApiResult()
}

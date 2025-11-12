package com.intellisoft.androidcuisine.utils

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

object SimpleApiTester {

    suspend fun testLogin(email: String, password: String, fcmToken: String): ApiTestResult {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("SimpleApiTester", "PRUEBA SIMPLE DE LOGIN")
                Log.d("SimpleApiTester", "URL: http://10.74.241.162:5000/api/auth/login")

                val client = OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .build()

                val jsonPayload = """
                {
                    "email": "$email",
                    "password": "$password",
                    "plataforma": "android",
                    "push_token": "$fcmToken"
                }
                """.trimIndent()

                Log.d("SimpleApiTester", "📤 Payload: $jsonPayload")

                val requestBody = jsonPayload.toRequestBody("application/json".toMediaType())

                val request = Request.Builder()
                    .url("http://10.74.241.162:5000/api/auth/login")
                    .post(requestBody)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .addHeader("User-Agent", "Android-CuisineApp/1.0")
                    .build()

                Log.d("SimpleApiTester", "🚀 Enviando petición...")

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                Log.d("SimpleApiTester", "📥 Código: ${response.code}")
                Log.d("SimpleApiTester", "📥 Mensaje: ${response.message}")
                Log.d("SimpleApiTester", "📥 Headers: ${response.headers}")
                Log.d("SimpleApiTester", "📥 Body: $responseBody")

                if (response.isSuccessful && responseBody != null) {
                    ApiTestResult.Success(responseBody)
                } else {
                    ApiTestResult.Error("HTTP ${response.code}: ${response.message}\nBody: $responseBody")
                }

            } catch (e: Exception) {
                Log.e("SimpleApiTester", "❌ Error: ${e.javaClass.simpleName}: ${e.message}")
                e.printStackTrace()
                ApiTestResult.Error("${e.javaClass.simpleName}: ${e.message}")
            }
        }
    }
}

sealed class ApiTestResult {
    data class Success(val data: String) : ApiTestResult()
    data class Error(val message: String) : ApiTestResult()
}

package com.intellisoft.androidcuisine.util

import android.content.Context
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

object FcmHelper {

    suspend fun getToken(context: Context): String {
        val sessionManager = SessionManager.getInstance(context)

        return try {
            // Obtener token directamente de Firebase
            val token = FirebaseMessaging.getInstance().token.await()

            // Guardarlo en SessionManager
            sessionManager.saveFcmToken(token)

            Log.d("FcmHelper", "Token FCM obtenido: ${token.take(20)}...")
            token
        } catch (e: Exception) {
            Log.e("FcmHelper", "Error obteniendo token FCM: ${e.message}")

            // Si falla, intentar usar el guardado
            val savedToken = sessionManager.getFcmToken()
            if (savedToken.isNotEmpty()) {
                Log.d("FcmHelper", "Usando token FCM guardado")
                savedToken
            } else {
                ""
            }
        }
    }
}
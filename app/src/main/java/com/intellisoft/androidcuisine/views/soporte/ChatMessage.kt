package com.intellisoft.androidcuisine.views.soporte

data class ChatMessage(
    val message: String,
    val isUser: Boolean, // true = mensaje del usuario (derecha), false = IA (izquierda)
    val timestamp: Long = System.currentTimeMillis()
)
package com.intellisoft.androidcuisine.views.soporte

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SoporteViewModel : ViewModel() {

    // --- CONFIGURACIÓN DE GEMINI ---
    // 1. Reemplaza esto con tu API KEY real de Google AI Studio
    private val API_KEY = "AIzaSyBDdJGQGoTaVpNPfgA7SPabHiZYOn6uwnQ"

    // 2. Definimos el contexto del sistema (Prompt del sistema)
    private val SYSTEM_CONTEXT = """
        Eres el asistente virtual oficial de la aplicación 'Android Cuisine'.
        Tu objetivo es ayudar a los usuarios (camareros, gerentes, cocineros) a usar la app.
        
        La app tiene estos módulos:
        - Sucursales: Gestión de locales.
        - Usuarios: Gestión de personal.
        - Mesas y Áreas: Configuración del restaurante.
        - Reservas y Órdenes: Toma de pedidos.
        - Cocina: Visualización de comandas.
        - Tickets de Incidencia: Para reportar fallos técnicos.
        - Buzón de Mejoras: Para sugerencias.
        
        REGLAS ESTRICTAS:
        1. Responde de forma breve, amable y profesional.
        2. SOLO responde preguntas relacionadas con la gestión de restaurantes, cocina o el uso de esta app.
        3. Si el usuario pregunta sobre temas ajenos (deportes, política, filosofía, matemáticas generales), responde: 
           "Lo siento, solo puedo responder consultas sobre la aplicación Android Cuisine y la gestión de tu restaurante."
        4. No inventes funcionalidades que no he mencionado.
    """.trimIndent()

    // Inicializar el modelo
    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash", // Modelo rápido y económico
        apiKey = API_KEY,
        systemInstruction = content { text(SYSTEM_CONTEXT) },
        generationConfig = generationConfig {
            temperature = 0.4f // Baja temperatura para respuestas más precisas y menos creativas
        }
    )

    // Iniciar el chat con historial
    private val chat = generativeModel.startChat()

    // --- ESTADO DEL CHAT ---
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun sendMessage(userMessage: String, onResponse: (String) -> Unit, onError: (String) -> Unit) {
        _isLoading.value = true

        viewModelScope.launch {
            try {
                // Enviamos mensaje al chat de Gemini (mantiene historial context)
                val response = chat.sendMessage(userMessage)
                val botResponse = response.text ?: "Lo siento, no pude procesar la respuesta."

                _isLoading.value = false
                onResponse(botResponse)

            } catch (e: Exception) {
                _isLoading.value = false
                android.util.Log.e("GEMINI_ERROR", "Error de conexión detallado: ${e.message}", e)              }
        }
    }
}
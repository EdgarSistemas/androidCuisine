package com.intellisoft.androidcuisine

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessaging

class CuisieneApp : ComponentActivity() {

    companion object {
        private const val TAG = "CuisieneApp"
    }

    private lateinit var tokenTextView: TextView

    // Launcher para solicitar permisos de notificación
    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Log.d(TAG, "Permiso de notificaciones concedido")
                Toast.makeText(this, "Permisos de notificación concedidos", Toast.LENGTH_SHORT).show()
            } else {
                Log.w(TAG, "Permiso de notificaciones denegado")
                Toast.makeText(this, "Permisos de notificación denegados", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Crear la UI programáticamente
        createSimpleUI()

        // Verificar permisos de notificación
        checkNotificationPermission()

        // Manejar datos del intent (cuando se abre desde una notificación)
        handleNotificationIntent()
    }

    /**
     * Crea una UI simple con un botón para generar token
     */
    private fun createSimpleUI() {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 100, 50, 50)
        }

        // Título
        val titleTextView = TextView(this).apply {
            text = "Cuisine App - FCM"
            textSize = 24f
            setPadding(0, 0, 0, 50)
        }

        // Botón para generar token
        val generateTokenButton = Button(this).apply {
            text = "Generar Token FCM"
            textSize = 18f
            setPadding(0, 20, 0, 20)
            setOnClickListener {
                generateFCMToken()
            }
        }

        // TextView para mostrar el token
        tokenTextView = TextView(this).apply {
            text = "Token: No generado"
            textSize = 14f
            setPadding(0, 30, 0, 0)
            setTextIsSelectable(true)
        }

        layout.addView(titleTextView)
        layout.addView(generateTokenButton)
        layout.addView(tokenTextView)

        setContentView(layout)
    }

    /**
     * Verifica permisos de notificación
     */
    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    Log.d(TAG, "Permisos de notificación ya concedidos")
                }
                else -> {
                    requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    /**
     * Genera un nuevo token FCM
     */
    private fun generateFCMToken() {
        Log.d(TAG, "Generando token FCM...")
        tokenTextView.text = "Generando token..."

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Error al obtener token FCM", task.exception)
                tokenTextView.text = "Error: ${task.exception?.message}"
                Toast.makeText(this@CuisieneApp, "Error al generar token", Toast.LENGTH_LONG).show()
                return@addOnCompleteListener
            }

            val token = task.result
            Log.d(TAG, "Token FCM generado: $token")

            // Mostrar token en la UI
            tokenTextView.text = "Token: $token"
            Toast.makeText(this@CuisieneApp, "Token generado exitosamente", Toast.LENGTH_SHORT).show()

            // AQUÍ ES DONDE ENVÍAS EL TOKEN A TU API
            Log.d(TAG, "TOKEN PARA ENVIAR A TU API: $token")
        }
    }



    /**
     * Maneja los datos cuando la app se abre desde una notificación
     */
    private fun handleNotificationIntent() {
        intent?.extras?.let { extras ->
            Log.d(TAG, "App abierta desde notificación")
            Toast.makeText(this, "App abierta desde notificación", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleNotificationIntent()
    }

    /**
     * Método público para obtener el token actual (para usar en tu API)
     */
    fun getCurrentFCMToken(callback: (String?) -> Unit) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(task.result)
            } else {
                callback(null)
            }
        }
    }
}
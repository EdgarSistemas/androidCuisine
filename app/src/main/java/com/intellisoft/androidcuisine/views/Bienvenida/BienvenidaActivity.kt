package com.intellisoft.androidcuisine.views.Bienvenida

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.messaging.FirebaseMessaging
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.utils.ApiResult
import com.intellisoft.androidcuisine.views.Main.MainActivity
import kotlinx.coroutines.launch
import com.intellisoft.androidcuisine.util.SessionManager

class BienvenidaActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bienvenida)

        // Inicializar SessionManager
        sessionManager = SessionManager.getInstance(this)

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnCrearCuenta = findViewById<Button>(R.id.btnCrearCuenta)

        // Listener para el botón de Login
        btnLogin.setOnClickListener {
            val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_login, null)
            val dialog = BottomSheetDialog(this)
            dialog.setContentView(bottomSheetView)

            // Obtener referencias a los campos del bottom sheet
            val inputEmail = bottomSheetView.findViewById<TextInputEditText>(R.id.inputEmail)
            val inputPassword = bottomSheetView.findViewById<TextInputEditText>(R.id.inputContraseña)
            val btnConfirmarLogin = bottomSheetView.findViewById<Button>(R.id.btnConfirmarLogin)

            // Pre-llenar campos para pruebas rápidas
            inputEmail.setText("jag030317@hotmail.com")
            inputPassword.setText("Soyjose123@")

            // LÓGICA REAL DE LOGIN CON API Y TOKEN FCM REAL
            btnConfirmarLogin.setOnClickListener {
                val email = inputEmail.text.toString().trim()
                val password = inputPassword.text.toString().trim()

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(this@BienvenidaActivity, "❌ Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // Deshabilitar botón mientras procesa
                btnConfirmarLogin.isEnabled = false
                btnConfirmarLogin.text = "Obteniendo token FCM..."

                Log.d("BienvenidaActivity", "🚀 Iniciando login desde bottom sheet")
                Log.d("BienvenidaActivity", "Email: $email")

                // OBTENER TOKEN FCM REAL DEL SERVICIO
                FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w("BienvenidaActivity", "Error al obtener token FCM", task.exception)

                        runOnUiThread {
                            btnConfirmarLogin.isEnabled = true
                            btnConfirmarLogin.text = "Entrar"
                            Toast.makeText(this@BienvenidaActivity, "❌ Error al obtener token de notificaciones", Toast.LENGTH_LONG).show()
                        }
                        return@addOnCompleteListener
                    }

                    val fcmToken = task.result
                    Log.d("BienvenidaActivity", "✅ Token FCM obtenido: $fcmToken")

                    runOnUiThread {
                        btnConfirmarLogin.text = "Conectando con API..."
                    }

                    // HACER PETICIÓN HTTP REAL CON TOKEN FCM REAL
                    lifecycleScope.launch {
                        try {
                            Log.d("BienvenidaActivity", "📡 Enviando petición con payload:")
                            Log.d("BienvenidaActivity", "Email: $email")
                            Log.d("BienvenidaActivity", "Password: ${password.take(3)}...")
                            Log.d("BienvenidaActivity", "Plataforma: android")
                            Log.d("BienvenidaActivity", "FCM Token: $fcmToken")

                            // Usar ApiClient directamente (Retrofit)
                            val loginRequest = com.intellisoft.androidcuisine.data.remote.dto.LoginRequest(
                                email = email,
                                password = password,
                                push_token = fcmToken
                            )

                            Log.d("BienvenidaActivity", "📡 Usando ApiClient con Retrofit")
                            val response = com.intellisoft.androidcuisine.data.remote.api.ApiClient.authService.login(loginRequest)

                            Log.d("BienvenidaActivity", "📥 Response code: ${response.code()}")
                            Log.d("BienvenidaActivity", "📥 Response message: ${response.message()}")

                            val result = if (response.isSuccessful) {
                                val loginResponse = response.body()
                                if (loginResponse != null) {
                                    val gson = com.google.gson.Gson()
                                    ApiResult.Success(gson.toJson(loginResponse))
                                } else {
                                    ApiResult.Error("Respuesta vacía del servidor")
                                }
                            } else {
                                val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                                ApiResult.Error("HTTP ${response.code()}: $errorBody")
                            }

                            when (result) {
                                is ApiResult.Success -> {
                                    Log.d("BienvenidaActivity", "✅ Login exitoso!")

                                    runOnUiThread {
                                        Toast.makeText(this@BienvenidaActivity, "✅ ¡Login exitoso!", Toast.LENGTH_SHORT).show()

                                        // Guardar datos del usuario usando SessionManager
                                        saveLoginDataWithSessionManager(result.data)

                                        // Navegar a MainActivity
                                        val intent = Intent(this@BienvenidaActivity, MainActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                        dialog.dismiss()
                                    }
                                }
                                is ApiResult.Error -> {
                                    Log.e("BienvenidaActivity", "❌ Error: ${result.message}")

                                    runOnUiThread {
                                        btnConfirmarLogin.isEnabled = true
                                        btnConfirmarLogin.text = "Entrar"
                                        Toast.makeText(this@BienvenidaActivity, "❌ Error: ${result.message}", Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("BienvenidaActivity", "❌ Error inesperado: ${e.message}")

                            runOnUiThread {
                                btnConfirmarLogin.isEnabled = true
                                btnConfirmarLogin.text = "Entrar"
                                Toast.makeText(this@BienvenidaActivity, "❌ Error de conexión: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            }

            dialog.show()
        }

        // Listener para el botón de Crear Cuenta
        btnCrearCuenta.setOnClickListener {
            val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_register, null)
            val dialog = BottomSheetDialog(this)
            dialog.setContentView(bottomSheetView)

            // TODO: Agregar lógica para el botón de registro aquí (similar al de login)

            dialog.show()
        }
    }

    private fun saveLoginDataWithSessionManager(responseJson: String) {
        try {
            val gson = com.google.gson.Gson()
            val loginResponse = gson.fromJson(responseJson, com.intellisoft.androidcuisine.data.remote.dto.LoginResponse::class.java)

            if (loginResponse != null) {
                // Usar SessionManager para guardar todos los datos
                sessionManager.saveLoginData(loginResponse)

                Log.d("BienvenidaActivity", "✅ Datos de login guardados con SessionManager")
                Log.d("BienvenidaActivity", "Usuario: ${loginResponse.user.nombre}")
                Log.d("BienvenidaActivity", "Bearer Token: ${sessionManager.getBearerToken()?.take(30)}...")
                Log.d("BienvenidaActivity", "Módulos: ${loginResponse.user.modulos.size}")
            }
        } catch (e: Exception) {
            Log.e("BienvenidaActivity", "Error guardando datos de login: ${e.message}")
        }
    }
}

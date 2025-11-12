package com.intellisoft.androidcuisine.views.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.messaging.FirebaseMessaging
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.views.Main.MainActivity
import kotlinx.coroutines.launch
import com.intellisoft.androidcuisine.utils.NetworkTester
import com.intellisoft.androidcuisine.utils.ApiResult

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // PRUEBA DIRECTA DE LOGIN CON API REAL
        Log.d("LoginActivity", "INICIANDO LOGIN AUTOMÁTICO CON API REAL")
        testWithNetworkTester()
    }

    private fun testWithNetworkTester() {
        Log.d("LoginActivity", "🧪 PROBANDO CON NETWORK TESTER")

        Toast.makeText(this, "🔄 Conectando con API...", Toast.LENGTH_SHORT).show()

        lifecycleScope.launch {
            try {
                val result = NetworkTester.testApiConnection(
                    email = "jag030317@hotmail.com",
                    password = "Soyjose123@",
                    fcmToken = "test_fcm_token"
                )

                when (result) {
                    is ApiResult.Success -> {
                        Log.d("LoginActivity", "✅ ¡CONEXIÓN EXITOSA!")
                        Log.d("LoginActivity", "Respuesta: ${result.data}")

                        runOnUiThread {
                            Toast.makeText(this@LoginActivity, "✅ ¡API conectada exitosamente!", Toast.LENGTH_LONG).show()

                            // Parsear respuesta y continuar
                            try {
                                val gson = com.google.gson.Gson()
                                val loginResponse = gson.fromJson(result.data, com.intellisoft.androidcuisine.data.remote.dto.LoginResponse::class.java)

                                if (loginResponse != null) {
                                    saveUserData(loginResponse)

                                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                            } catch (e: Exception) {
                                Log.e("LoginActivity", "Error parseando: ${e.message}")
                                Toast.makeText(this@LoginActivity, "❌ Error parseando respuesta: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                    is ApiResult.Error -> {
                        Log.e("LoginActivity", "❌ ERROR: ${result.message}")
                        runOnUiThread {
                            Toast.makeText(this@LoginActivity, "❌ Error: ${result.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("LoginActivity", "❌ Error en testWithNetworkTester: ${e.message}")
                runOnUiThread {
                    Toast.makeText(this@LoginActivity, "❌ Error inesperado: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun saveUserData(loginResponse: com.intellisoft.androidcuisine.data.remote.dto.LoginResponse) {
        val sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("access_token", loginResponse.access_token)
            putString("user_name", loginResponse.user.nombre)
            putString("user_email", loginResponse.user.email)
            putBoolean("is_admin", loginResponse.user.es_admin)
            // Guardar módulos como JSON string
            val gson = com.google.gson.Gson()
            val modulosJson = gson.toJson(loginResponse.user.modulos)
            putString("user_modules", modulosJson)
            apply()
        }
    }
}
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
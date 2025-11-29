// edgarsistemas/androidcuisine/androidCuisine-dev-cesar/app/src/main/java/com/intellisoft/androidcuisine/views/auth/RestablecerPasswordActivity.kt
package com.intellisoft.androidcuisine.views.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.views.Bienvenida.BienvenidaActivity // Para volver al inicio

class RestablecerPasswordActivity : AppCompatActivity() {

    private val viewModel: RecuperacionViewModel by viewModels()
    private lateinit var btnRestablecer: Button
    private lateinit var inputNewPassword: TextInputEditText
    private lateinit var inputConfirmPassword: TextInputEditText
    private var email: String = ""
    private var codigo: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restablecer_password)

        // 1. Recuperar email y código de la actividad anterior
        email = intent.getStringExtra("EMAIL_RECUPERACION") ?: ""
        codigo = intent.getStringExtra("CODIGO_VERIFICACION") ?: ""

        if (email.isEmpty() || codigo.isEmpty()) {
            Toast.makeText(this, "Error de sesión. Intenta de nuevo.", Toast.LENGTH_LONG).show()
            navigateToBienvenida()
            return
        }

        // 2. Encontrar las vistas
        btnRestablecer = findViewById(R.id.btnRestablecer)
        val ivBack: ImageView = findViewById(R.id.ivBack)
        inputNewPassword = findViewById(R.id.inputNewPassword)
        inputConfirmPassword = findViewById(R.id.inputConfirmPassword)

        // 3. Observar el estado del restablecimiento
        observeRestablecerState()

        // 4. Listener para el botón "Restablecer Contraseña"
        btnRestablecer.setOnClickListener {
            val newPassword = inputNewPassword.text.toString().trim()
            val confirmPassword = inputConfirmPassword.text.toString().trim()

            viewModel.restablecerPassword(codigo, email, newPassword, confirmPassword)
        }

        // 5. Listener para el botón de regresar
        ivBack.setOnClickListener {
            finish() // Cierra esta actividad
        }
    }

    private fun observeRestablecerState() {
        viewModel.restablecerState.observe(this) { state ->
            when (state) {
                RestablecerState.Loading -> {
                    btnRestablecer.isEnabled = false
                    btnRestablecer.text = "Restableciendo..."
                }
                is RestablecerState.Success -> {
                    btnRestablecer.isEnabled = true
                    btnRestablecer.text = "Restablecer Contraseña"
                    Toast.makeText(this, "✅ ${state.message}", Toast.LENGTH_LONG).show()

                    // Finaliza el flujo de recuperación y vuelve a la pantalla de bienvenida/login
                    navigateToBienvenida()
                }
                is RestablecerState.Error -> {
                    btnRestablecer.isEnabled = true
                    btnRestablecer.text = "Restablecer Contraseña"
                    Toast.makeText(this, "❌ Error: ${state.message}", Toast.LENGTH_LONG).show()
                }
                RestablecerState.Idle -> {
                    btnRestablecer.isEnabled = true
                    btnRestablecer.text = "Restablecer Contraseña"
                }
            }
        }
    }

    private fun navigateToBienvenida() {
        // Redirige a la pantalla de Bienvenida y limpia el stack de actividades
        val intent = Intent(this, BienvenidaActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}
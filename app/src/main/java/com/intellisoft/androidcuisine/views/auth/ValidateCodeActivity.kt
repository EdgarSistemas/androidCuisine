package com.intellisoft.androidcuisine.views.auth

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.intellisoft.androidcuisine.R

class ValidateCodeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_validate_code)

        // 1. Encontrar las vistas
        val btnValidate: Button = findViewById(R.id.btnValidateCode)
        val ivBack: ImageView = findViewById(R.id.ivBack)

        // 2. Listener para el botón "Validar y Enviar"
        btnValidate.setOnClickListener {
            // ---
            // Aquí, en el futuro, pondrías tu lógica para
            // validar el código contra tu API.
            // ---

            Toast.makeText(this, "Validando código...", Toast.LENGTH_SHORT).show()

            // Si es exitoso, podrías cerrar esta pantalla (finish())
            // y la anterior (RecoverPasswordActivity), o navegar a una
            // pantalla de "Crear Nueva Contraseña".
        }

        // 3. Listener para el botón de regresar
        ivBack.setOnClickListener {
            finish() // Cierra esta actividad
        }
    }
}
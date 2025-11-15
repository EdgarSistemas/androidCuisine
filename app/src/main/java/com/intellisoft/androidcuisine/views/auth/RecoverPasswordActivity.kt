package com.intellisoft.androidcuisine.views.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.intellisoft.androidcuisine.R

class RecoverPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recover_password)

        // 1. Encontrar las vistas del layout
        val btnRequestCode: Button = findViewById(R.id.btnRequestCode)
        val ivBack: ImageView = findViewById(R.id.ivBack)

        // 2. Listener para el botón "Solicitar Código" (como pediste)
        btnRequestCode.setOnClickListener {
            // ---
            // Aquí, en el futuro, pondrías tu lógica para
            // llamar a la API y enviar el correo.
            // ---

            // Por ahora, solo iniciamos la siguiente actividad
            val intent = Intent(this, ValidateCodeActivity::class.java)
            startActivity(intent)
        }

        // 3. Listener para el botón de regresar
        ivBack.setOnClickListener {
            finish() // Cierra esta actividad y regresa a la anterior
        }
    }
}
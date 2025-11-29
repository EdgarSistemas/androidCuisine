// edgarsistemas/androidcuisine/androidCuisine-dev-cesar/app/src/main/java/com/intellisoft/androidcuisine/views/auth/RecoverPasswordActivity.kt
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

class RecoverPasswordActivity : AppCompatActivity() {

    private val viewModel: RecuperacionViewModel by viewModels()
    private lateinit var btnRequestCode: Button
    private lateinit var inputEmail: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recover_password)

        // 1. Encontrar las vistas
        btnRequestCode = findViewById(R.id.btnRequestCode)
        val ivBack: ImageView = findViewById(R.id.ivBack)
        inputEmail = findViewById(R.id.inputEmailRecover)

        // 2. Observar el estado de la solicitud
        observeSolicitudState()

        // 3. Listener para el botón "Solicitar Código"
        btnRequestCode.setOnClickListener {
            val email = inputEmail.text.toString().trim()
            viewModel.solicitarCodigo(email)
        }

        // 4. Listener para el botón de regresar
        ivBack.setOnClickListener {
            finish() // Cierra esta actividad
        }
    }

    private fun observeSolicitudState() {
        viewModel.solicitudState.observe(this) { state ->
            when (state) {
                SolicitudState.Loading -> {
                    btnRequestCode.isEnabled = false
                    btnRequestCode.text = "Solicitando..."
                }
                is SolicitudState.Success -> {
                    btnRequestCode.isEnabled = true
                    btnRequestCode.text = "Solicitar Código"
                    Toast.makeText(this, "Código enviado a: ${state.email}", Toast.LENGTH_LONG).show()

                    // NAVEGACIÓN EXITOSA: Pasar el email a ValidateCodeActivity
                    val intent = Intent(this, ValidateCodeActivity::class.java).apply {
                        putExtra("EMAIL_RECUPERACION", state.email)
                    }
                    startActivity(intent)
                    finish() // Cierra RecoverPasswordActivity
                    viewModel.resetSolicitudState() // Limpiar el estado
                }
                is SolicitudState.Error -> {
                    btnRequestCode.isEnabled = true
                    btnRequestCode.text = "Solicitar Código"
                    Toast.makeText(this, "❌ Error: ${state.message}", Toast.LENGTH_LONG).show()
                }
                SolicitudState.Idle -> {
                    btnRequestCode.isEnabled = true
                    btnRequestCode.text = "Solicitar Código"
                }
            }
        }
    }
}
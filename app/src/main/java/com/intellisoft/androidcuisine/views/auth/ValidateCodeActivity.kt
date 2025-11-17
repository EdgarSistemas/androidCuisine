// edgarsistemas/androidcuisine/androidCuisine-dev-cesar/app/src/main/java/com/intellisoft/androidcuisine/views/auth/ValidateCodeActivity.kt
package com.intellisoft.androidcuisine.views.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.intellisoft.androidcuisine.R

class ValidateCodeActivity : AppCompatActivity() {

    private val viewModel: RecuperacionViewModel by viewModels()
    private lateinit var btnValidate: Button
    private lateinit var inputValidationCode: TextInputEditText
    private lateinit var tvValidateInstructions: TextView
    private var email: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_validate_code)

        // 1. Recuperar email de la actividad anterior
        email = intent.getStringExtra("EMAIL_RECUPERACION") ?: run {
            Toast.makeText(this, "Error: Correo no recibido.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // 2. Encontrar las vistas
        btnValidate = findViewById(R.id.btnValidateCode)
        val ivBack: ImageView = findViewById(R.id.ivBack)
        inputValidationCode = findViewById(R.id.inputValidationCode)
        tvValidateInstructions = findViewById(R.id.tvValidateInstructions)

        tvValidateInstructions.text = "Introduce el código de 6 dígitos enviado a: $email"

        // 3. Observar el estado de la verificación
        observeVerificacionState()

        // 4. Listener para el botón "Validar y Enviar"
        btnValidate.setOnClickListener {
            val codigo = inputValidationCode.text.toString().trim()
            viewModel.verificarCodigo(codigo, email)
        }

        // 5. Listener para el botón de regresar
        ivBack.setOnClickListener {
            finish() // Cierra esta actividad
        }
    }

    private fun observeVerificacionState() {
        viewModel.verificacionState.observe(this) { state ->
            when (state) {
                VerificacionState.Loading -> {
                    btnValidate.isEnabled = false
                    btnValidate.text = "Verificando..."
                }
                is VerificacionState.Success -> {
                    btnValidate.isEnabled = true
                    btnValidate.text = "Validar y Enviar"
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()

                    // NAVEGACIÓN EXITOSA: Pasar el email y el código a RestablecerPasswordActivity
                    val intent = Intent(this, RestablecerPasswordActivity::class.java).apply {
                        putExtra("EMAIL_RECUPERACION", state.email)
                        putExtra("CODIGO_VERIFICACION", state.codigo)
                    }
                    startActivity(intent)
                    finish() // Cierra ValidateCodeActivity
                    viewModel.resetVerificacionState() // Limpiar el estado
                }
                is VerificacionState.Error -> {
                    btnValidate.isEnabled = true
                    btnValidate.text = "Validar y Enviar"
                    Toast.makeText(this, "❌ Error: ${state.message}", Toast.LENGTH_LONG).show()
                }
                VerificacionState.Idle -> {
                    btnValidate.isEnabled = true
                    btnValidate.text = "Validar y Enviar"
                }
            }
        }
    }
}
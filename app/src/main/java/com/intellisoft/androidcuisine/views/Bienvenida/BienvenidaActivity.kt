package com.intellisoft.androidcuisine.views.Bienvenida

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.views.Bienvenida.BienvenidaViewModel
import com.intellisoft.androidcuisine.views.Main.MainActivity
import com.intellisoft.androidcuisine.views.auth.RecoverPasswordActivity

class BienvenidaActivity : AppCompatActivity() {

    private val viewModel: BienvenidaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observeSession()
        viewModel.checkSession()
    }

    private fun observeSession() {
        viewModel.isSessionActive.observe(this) { isActive ->
            if (isActive) {
                navigateToMain()
            } else {
                showBienvenidaUI()
            }
        }
    }

    private fun showBienvenidaUI() {
        setContentView(R.layout.activity_bienvenida)

        findViewById<Button>(R.id.btnLogin).setOnClickListener {
            showLoginBottomSheet()
        }

        findViewById<Button>(R.id.btnCrearCuenta).setOnClickListener {
            showRegisterBottomSheet()
        }
    }

    private fun showLoginBottomSheet() {
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_login, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(bottomSheetView)

        val inputEmail = bottomSheetView.findViewById<TextInputEditText>(R.id.inputEmail)
        val inputPassword = bottomSheetView.findViewById<TextInputEditText>(R.id.inputContraseña)
        val btnConfirmarLogin = bottomSheetView.findViewById<Button>(R.id.btnConfirmarLogin)

        // 💡 CORRECCIÓN: Declarar y encontrar la vista tvForgotPassword
        val tvForgotPassword = bottomSheetView.findViewById<TextView>(R.id.tvForgotPassword)

        // Pre-llenar para pruebas
        inputEmail.setText("jag030317@hotmail.com")
        inputPassword.setText("Soyjose123@")

        // AGREGAR LISTENER PARA OLVIDÉ CONTRASEÑA
        tvForgotPassword.setOnClickListener {
            dialog.dismiss() // Cierra el Bottom Sheet
            // Asegúrate de que RecuperacionViewModel esté importado para que el intent funcione
            val intent = Intent(this, RecoverPasswordActivity::class.java)
            startActivity(intent)
        }

        observeLoginState(btnConfirmarLogin, dialog)

        btnConfirmarLogin.setOnClickListener {
            val email = inputEmail.text.toString().trim()
            val password = inputPassword.text.toString().trim()
            viewModel.login(email, password)
        }

        dialog.show()
    }
    private fun observeLoginState(button: Button, dialog: BottomSheetDialog) {
        viewModel.loginState.observe(this) { state ->
            when (state) {
                is LoginState.Loading -> {
                    button.isEnabled = false
                    button.text = "Conectando..."
                }
                is LoginState.Success -> {
                    Toast.makeText(this, "¡Bienvenido ${state.userName}!", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    navigateToMain()
                }
                is LoginState.Error -> {
                    button.isEnabled = true
                    button.text = "Entrar"
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun showRegisterBottomSheet() {
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_register, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(bottomSheetView)
        dialog.show()
    }
}
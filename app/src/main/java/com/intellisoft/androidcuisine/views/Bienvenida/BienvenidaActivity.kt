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

import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.Manifest
import com.intellisoft.androidcuisine.views.auth.PermisosObligatoriosActivity

class BienvenidaActivity : AppCompatActivity() {

    private val viewModel: BienvenidaViewModel by viewModels()

    // 🔴 PERMISOS ESENCIALES OBLIGATORIOS
    private val REQUIRED_PERMISSIONS = mutableListOf(
        Manifest.permission.CAMERA,
        Manifest.permission.INTERNET // Ya es normal, pero se incluye para un chequeo lógico.
    ).apply {
        // Añadir permisos de almacenamiento según la versión de Android
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.READ_MEDIA_IMAGES)
            add(Manifest.permission.READ_MEDIA_VIDEO)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }.toTypedArray()


    // Launcher para solicitar los permisos
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allGranted = permissions.entries.all {
                // Solo nos importan los permisos que se pueden denegar
                it.value || !isRuntimePermission(it.key)
            }
            if (allGranted) {
                // 🟢 Éxito: Todos los permisos esenciales concedidos
                startAppFlow()
            } else {
                // ❌ Falla: Al menos un permiso esencial denegado
                // 🛑 CORRECCIÓN: NAVEGAR A LA NUEVA ACTIVITY
                navigateToFatalPermissionScreen()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
// 1. Iniciar la verificación y solicitud de permisos
        checkAndRequestPermissions()
        observeSession()
        viewModel.checkSession()
    }

    // El flujo principal de la app se inicia SÓLO si los permisos son correctos
    private fun startAppFlow() {
        observeSession()
        viewModel.checkSession()
    }

    private fun checkAndRequestPermissions() {
        val permissionsToRequest = REQUIRED_PERMISSIONS.filter {
            // No solicitamos permisos 'normales' (como INTERNET), solo los de 'runtime'
            isRuntimePermission(it) && ContextCompat.checkSelfPermission(
                this,
                it
            ) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isEmpty()) {
            // 🟢 Todos los permisos de runtime ya están concedidos
            startAppFlow()
        } else {
            // 🟡 Pedir los permisos faltantes
            requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    private fun isRuntimePermission(permission: String): Boolean {
        // Los permisos de INTERNET, WAKE_LOCK son de tipo 'normal' y no necesitan ser solicitados en runtime
        return when (permission) {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.POST_NOTIFICATIONS -> true

            else -> false
        }
    }

    // 🛑 NUEVO MÉTODO DE NAVEGACIÓN
    private fun navigateToFatalPermissionScreen() {
        val intent = Intent(this, PermisosObligatoriosActivity::class.java).apply {
            // Asegura que el usuario no pueda volver atrás al flujo de Bienvenida
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
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
                    Toast.makeText(this, "¡Bienvenido ${state.userName}!", Toast.LENGTH_SHORT)
                        .show()
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
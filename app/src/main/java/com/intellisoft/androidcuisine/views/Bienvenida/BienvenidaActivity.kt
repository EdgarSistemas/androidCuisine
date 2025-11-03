package com.intellisoft.androidcuisine.views.Bienvenida

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.views.Main.MainActivity

class BienvenidaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bienvenida)

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnCrearCuenta = findViewById<Button>(R.id.btnCrearCuenta)

        // Listener para el botón de Login
        btnLogin.setOnClickListener {
            val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_login, null)
            val dialog = BottomSheetDialog(this)
            dialog.setContentView(bottomSheetView)

            // --- INICIO DE LA MODIFICACIÓN ---
            // Encontrar el botón "Entrar" DENTRO del bottom sheet
            val btnConfirmarLogin = bottomSheetView.findViewById<Button>(R.id.btnConfirmarLogin)
            btnConfirmarLogin.setOnClickListener {
                // TODO: Aquí deberías agregar tu lógica de validación de email y contraseña

                // Si el login es exitoso, navegar a MainActivity
                val intent = Intent(this@BienvenidaActivity, MainActivity::class.java)
                startActivity(intent)

                // Cerrar esta actividad (BienvenidaActivity) para que el usuario no pueda volver
                finish()
                // Cerrar el BottomSheet
                dialog.dismiss()
            }
            // --- FIN DE LA MODIFICACIÓN ---

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
}

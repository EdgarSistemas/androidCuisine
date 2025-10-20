package com.intellisoft.androidcuisine.views.Bienvenida

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.intellisoft.androidcuisine.R

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
            dialog.show()
        }

        // Listener para el botón de Crear Cuenta
        btnCrearCuenta.setOnClickListener {
            val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_register, null)
            val dialog = BottomSheetDialog(this)
            dialog.setContentView(bottomSheetView)
            dialog.show()
        }
    }
}
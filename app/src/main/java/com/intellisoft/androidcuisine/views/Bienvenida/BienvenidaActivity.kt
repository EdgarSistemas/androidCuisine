package com.intellisoft.androidcuisine.views.Bienvenida

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.intellisoft.androidcuisine.R
//
// AQUÍ ESTÁ LA CORRECCIÓN:
// Añadimos ".Bienvenida" a la ruta de importación
//
import com.intellisoft.androidcuisine.views.MainActivity

class BienvenidaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bienvenida)

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnCrearCuenta = findViewById<Button>(R.id.btnCrearCuenta)

        btnLogin.setOnClickListener {
            val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_login, null)
            val dialog = BottomSheetDialog(this)
            dialog.setContentView(bottomSheetView)

            val etUsuario = bottomSheetView.findViewById<TextInputEditText>(R.id.inputEmail)
            val etContrasena = bottomSheetView.findViewById<TextInputEditText>(R.id.inputContraseña)
            val btnConfirmarLogin = bottomSheetView.findViewById<Button>(R.id.btnConfirmarLogin)

            btnConfirmarLogin.setOnClickListener {
                val usuario = etUsuario.text.toString()
                val contrasena = etContrasena.text.toString()

                if (usuario == "admin" && contrasena == "admin") {
                    Toast.makeText(this, "Login exitoso", Toast.LENGTH_SHORT).show()

                    // Ahora este Intent usará la importación_ CORRECTA
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)

                    dialog.dismiss()
                    finish()

                } else {
                    Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                }
            }

            dialog.show()
        }

        btnCrearCuenta.setOnClickListener {
            val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_register, null)
            val dialog = BottomSheetDialog(this)
            dialog.setContentView(bottomSheetView)
            dialog.show()
        }
    }
}


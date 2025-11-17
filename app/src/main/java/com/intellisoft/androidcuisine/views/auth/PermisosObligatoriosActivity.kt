// edgarsistemas/androidcuisine/androidCuisine-dev-cesar/app/src/main/java/com/intellisoft/androidcuisine/views/auth/PermisosObligatoriosActivity.kt
package com.intellisoft.androidcuisine.views.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.views.Bienvenida.BienvenidaActivity

class PermisosObligatoriosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permisos_obligatorios)

        val btnReintentar = findViewById<Button>(R.id.btnReintentarPermisos)
        val btnCerrar = findViewById<Button>(R.id.btnCerrarApp)

        // Botón Reintentar: Intenta abrir la configuración de la app para que el usuario acepte
        btnReintentar.setOnClickListener {
            // Abrir la configuración de la app para que el usuario pueda ir y habilitar los permisos
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", packageName, null)
                addCategory(Intent.CATEGORY_DEFAULT)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)

            // Luego redirigimos a BienvenidaActivity para que haga la nueva verificación
            val backIntent = Intent(this, BienvenidaActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(backIntent)
            finish()
        }

        // Botón Cerrar: Fuerza la salida de la aplicación
        btnCerrar.setOnClickListener {
            finishAndRemoveTask()
        }
    }

//    override fun onBackPressed() {
//        // Bloquear el back button para obligar al usuario a tomar una decisión
//    }
}
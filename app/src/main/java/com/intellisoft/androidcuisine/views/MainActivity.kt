package com.intellisoft.androidcuisine.views // <-- ¡Asegúrate que el paquete sea este!

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.intellisoft.androidcuisine.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Esta línea conecta tu clase Kotlin con su archivo de diseño XML
        setContentView(R.layout.activity_main)
    }
}

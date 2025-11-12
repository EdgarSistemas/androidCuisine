package com.intellisoft.androidcuisine

import android.app.Application
import android.util.Log
import com.intellisoft.androidcuisine.data.remote.api.ApiClient

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Log.d("MyApplication", "🚀 Inicializando aplicación...")

        // Inicializar ApiClient con contexto
        ApiClient.init(this)

        Log.d("MyApplication", "✅ ApiClient inicializado")
    }
}

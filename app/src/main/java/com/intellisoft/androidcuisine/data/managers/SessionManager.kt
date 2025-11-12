package com.intellisoft.androidcuisine.data.managers

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.intellisoft.androidcuisine.data.remote.dto.LoginResponse
import com.intellisoft.androidcuisine.data.remote.dto.Modulo

class SessionManager(context: Context) {

    companion object {
        private const val PREF_NAME = "user_session"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_TOKEN_TYPE = "token_type"
        private const val KEY_EXPIRES_IN = "expires_in"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_APELLIDO = "user_apellido"
        private const val KEY_USER_TELEFONO = "user_telefono"
        private const val KEY_IS_ADMIN = "is_admin"
        private const val KEY_ES_CLIENTE = "es_cliente"
        private const val KEY_ACEPTA_MARKETING = "acepta_marketing"
        private const val KEY_MOSTRAR_EMPRESAS = "mostrar_empresas"
        private const val KEY_TIPO_ACCESO = "tipo_acceso"
        private const val KEY_USER_MODULES = "user_modules"
        private const val KEY_LOGIN_TIME = "login_time"

        @Volatile
        private var INSTANCE: SessionManager? = null

        fun getInstance(context: Context): SessionManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SessionManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    /**
     * Guarda toda la información de login
     */
    fun saveLoginData(loginResponse: LoginResponse) {
        val editor = sharedPreferences.edit()

        // Token info
        editor.putString(KEY_ACCESS_TOKEN, loginResponse.access_token)
        editor.putString(KEY_TOKEN_TYPE, loginResponse.token_type)
        editor.putInt(KEY_EXPIRES_IN, loginResponse.expires_in)
        editor.putLong(KEY_LOGIN_TIME, System.currentTimeMillis())

        // User info
        val user = loginResponse.user
        editor.putInt(KEY_USER_ID, user.id)
        editor.putString(KEY_USER_NAME, user.nombre)
        editor.putString(KEY_USER_EMAIL, user.email)
        editor.putString(KEY_USER_APELLIDO, user.apellido)
        editor.putString(KEY_USER_TELEFONO, user.telefono)
        editor.putBoolean(KEY_IS_ADMIN, user.es_admin)
        editor.putBoolean(KEY_ES_CLIENTE, user.es_cliente)
        editor.putBoolean(KEY_ACEPTA_MARKETING, user.acepta_marketing)
        editor.putBoolean(KEY_MOSTRAR_EMPRESAS, user.mostrar_empresas)
        editor.putString(KEY_TIPO_ACCESO, user.tipo_acceso)

        // Modules as JSON
        val modulesJson = gson.toJson(user.modulos)
        editor.putString(KEY_USER_MODULES, modulesJson)

        editor.apply()

        Log.d("SessionManager", "✅ Datos de sesión guardados")
        Log.d("SessionManager", "Usuario: ${user.nombre}")
        Log.d("SessionManager", "Token: ${loginResponse.access_token.take(20)}...")
        Log.d("SessionManager", "Módulos: ${user.modulos.size}")
    }

    /**
     * Obtiene el token Bearer completo para Authorization header
     */
    fun getBearerToken(): String? {
        val tokenType = sharedPreferences.getString(KEY_TOKEN_TYPE, "Bearer") ?: "Bearer"
        val accessToken = sharedPreferences.getString(KEY_ACCESS_TOKEN, null)

        return if (accessToken != null) {
            "$tokenType $accessToken"
        } else {
            null
        }
    }

    /**
     * Obtiene solo el access token
     */
    fun getAccessToken(): String? {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null)
    }

    /**
     * Verifica si hay una sesión activa
     */
    fun isLoggedIn(): Boolean {
        val token = getAccessToken()
        return !token.isNullOrEmpty() && !isTokenExpired()
    }

    /**
     * Verifica si el token ha expirado
     */
    fun isTokenExpired(): Boolean {
        val loginTime = sharedPreferences.getLong(KEY_LOGIN_TIME, 0)
        val expiresIn = sharedPreferences.getInt(KEY_EXPIRES_IN, 0)

        if (loginTime == 0L || expiresIn == 0) {
            return true
        }

        val currentTime = System.currentTimeMillis()
        val expirationTime = loginTime + (expiresIn * 1000L) // expiresIn está en segundos

        return currentTime >= expirationTime
    }

    /**
     * Obtiene datos del usuario
     */
    fun getUserData(): UserData? {
        if (!isLoggedIn()) return null

        return UserData(
            id = sharedPreferences.getInt(KEY_USER_ID, 0),
            nombre = sharedPreferences.getString(KEY_USER_NAME, "") ?: "",
            email = sharedPreferences.getString(KEY_USER_EMAIL, "") ?: "",
            apellido = sharedPreferences.getString(KEY_USER_APELLIDO, "") ?: "",
            telefono = sharedPreferences.getString(KEY_USER_TELEFONO, "") ?: "",
            esAdmin = sharedPreferences.getBoolean(KEY_IS_ADMIN, false),
            esCliente = sharedPreferences.getBoolean(KEY_ES_CLIENTE, false),
            aceptaMarketing = sharedPreferences.getBoolean(KEY_ACEPTA_MARKETING, false),
            mostrarEmpresas = sharedPreferences.getBoolean(KEY_MOSTRAR_EMPRESAS, false),
            tipoAcceso = sharedPreferences.getString(KEY_TIPO_ACCESO, "") ?: ""
        )
    }

    /**
     * Obtiene módulos del usuario
     */
    fun getUserModules(): List<Modulo> {
        val modulesJson = sharedPreferences.getString(KEY_USER_MODULES, "[]") ?: "[]"
        return try {
            val type = object : com.google.gson.reflect.TypeToken<List<Modulo>>() {}.type
            gson.fromJson(modulesJson, type) ?: emptyList()
        } catch (e: Exception) {
            Log.e("SessionManager", "Error parseando módulos: ${e.message}")
            emptyList()
        }
    }

    /**
     * Limpia toda la sesión
     */
    fun clearSession() {
        sharedPreferences.edit().clear().apply()
        Log.d("SessionManager", "🚮 Sesión limpiada")
    }

    /**
     * Actualiza solo el token (para refresh token)
     */
    fun updateToken(newToken: String, expiresIn: Int) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_ACCESS_TOKEN, newToken)
        editor.putInt(KEY_EXPIRES_IN, expiresIn)
        editor.putLong(KEY_LOGIN_TIME, System.currentTimeMillis())
        editor.apply()

        Log.d("SessionManager", "🔄 Token actualizado")
    }

    /**
     * Obtiene el ID del usuario almacenado en la sesión
     */
    fun getUserId(): Int {
        return sharedPreferences.getInt(KEY_USER_ID, -1)
    }
}

/**
 * Clase de datos para información del usuario
 */
data class UserData(
    val id: Int,
    val nombre: String,
    val email: String,
    val apellido: String,
    val telefono: String,
    val esAdmin: Boolean,
    val esCliente: Boolean,
    val aceptaMarketing: Boolean,
    val mostrarEmpresas: Boolean,
    val tipoAcceso: String
)

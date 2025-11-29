package com.intellisoft.androidcuisine.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
//import com.google.common.base.StandardSystemProperty.USER_NAME
import com.google.gson.Gson
import com.intellisoft.androidcuisine.data.remote.dto.LoginResponse
import com.intellisoft.androidcuisine.data.remote.dto.ModuloDto
import com.intellisoft.androidcuisine.data.remote.dto.RolDto
import com.intellisoft.androidcuisine.data.remote.dto.SucursalDto


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
        private const val KEY_FCM_TOKEN = "fcm_token"
        private const val KEY_USER_ROLES = "user_roles"
        private const val KEY_USER_SUCURSALES = "user_sucursales"

        private const val KEY_SELECTED_SUCURSAL_ID = "selected_sucursal_id"



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

    fun saveLoginData(loginResponse: LoginResponse) {
        val editor = sharedPreferences.edit()

        editor.putString(KEY_ACCESS_TOKEN, loginResponse.access_token)
        editor.putString(KEY_TOKEN_TYPE, loginResponse.token_type)
        editor.putInt(KEY_EXPIRES_IN, loginResponse.expires_in)
        editor.putLong(KEY_LOGIN_TIME, System.currentTimeMillis())

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

        // Guardar módulos, roles y sucursales como JSON
        editor.putString(KEY_USER_MODULES, gson.toJson(user.modulos))
        editor.putString(KEY_USER_ROLES, gson.toJson(user.roles))
        editor.putString(KEY_USER_SUCURSALES, gson.toJson(user.sucursales))



        editor.apply()
        Log.d("SessionManager", "Sesión guardada para: ${user.nombre}")
    }

    fun getBearerToken(): String? {
        val tokenType = sharedPreferences.getString(KEY_TOKEN_TYPE, "Bearer") ?: "Bearer"
        val accessToken = sharedPreferences.getString(KEY_ACCESS_TOKEN, null)
        return if (accessToken != null) "$tokenType $accessToken" else null
    }

    fun getAccessToken(): String? {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null)
    }

    fun isLoggedIn(): Boolean {
        val token = getAccessToken()
        return !token.isNullOrEmpty() && !isTokenExpired()
    }

    fun isTokenExpired(): Boolean {
        val loginTime = sharedPreferences.getLong(KEY_LOGIN_TIME, 0)
        val expiresIn = sharedPreferences.getInt(KEY_EXPIRES_IN, 0)
        if (loginTime == 0L || expiresIn == 0) return true
        val expirationTime = loginTime + (expiresIn * 1000L)
        return System.currentTimeMillis() >= expirationTime
    }

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




    fun getUserModules(): List<ModuloDto> {
        val modulesJson = sharedPreferences.getString(KEY_USER_MODULES, "[]") ?: "[]"
        return try {
            val type = object : com.google.gson.reflect.TypeToken<List<ModuloDto>>() {}.type
            gson.fromJson(modulesJson, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun clearSession() {
        sharedPreferences.edit().clear().apply()
        Log.d("SessionManager", "🚮 Sesión limpiada")
    }

    fun getUserId(): Int = sharedPreferences.getInt(KEY_USER_ID, -1)

    fun saveFcmToken(token: String) {
        sharedPreferences.edit().putString(KEY_FCM_TOKEN, token).apply()
        Log.d("SessionManager", "🔔 FCM Token guardado")
    }

    fun getFcmToken(): String {
        return sharedPreferences.getString(KEY_FCM_TOKEN, "") ?: ""
    }

    fun getUserRoles(): List<RolDto> {
        val rolesJson = sharedPreferences.getString(KEY_USER_ROLES, "[]") ?: "[]"
        return try {
            val type = object : com.google.gson.reflect.TypeToken<List<RolDto>>() {}.type
            gson.fromJson(rolesJson, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getUserSucursales(): List<SucursalDto> {
        val sucursalesJson = sharedPreferences.getString(KEY_USER_SUCURSALES, "[]") ?: "[]"
        return try {
            val type = object : com.google.gson.reflect.TypeToken<List<SucursalDto>>() {}.type
            gson.fromJson(sucursalesJson, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getPrimaryRole(): String {
        val roles = getUserRoles()
        return if (roles.isNotEmpty()) roles[0].nombre else "Usuario"
    }

    //FUNCION AGREGADA POR CESAR
    fun updateUserData(nombre: String, apellido: String, email: String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_USER_NAME, nombre)
        editor.putString(KEY_USER_APELLIDO, apellido)
        editor.putString(KEY_USER_EMAIL, email)
        editor.apply()
        Log.d("SessionManager", "♻️ Datos actualizados")
    }

    //OBTENER SUCURSALES
    /**
     * Guarda el ID de la sucursal actual de trabajo.
     * Si es Empleado/Gerente: Se guarda al hacer login automáticamente.
     * Si es Admin: Se guarda cuando el Admin selecciona la sucursal en el UI.
     */
    fun saveSucursalId(sucursalId: Int) {
        // Corrección: Usamos sharedPreferences.edit() directamente
        sharedPreferences.edit().putInt(KEY_SELECTED_SUCURSAL_ID, sucursalId).apply()
        Log.d("SessionManager", "🏢 Sucursal seleccionada guardada: $sucursalId")
    }

//    fun getSucursalId(): Int {
//        val sucursales = getUserSucursales()
//
//        // Aquí está la magia: "Si la lista no está vacía, devuelve la PRIMERA (índice 0)"
//        return if (sucursales.isNotEmpty()) sucursales[0].id_sucursal else 0
//    }

    fun getSucursalId(): Int {
        val sucursales = getUserSucursales()

        // Si no hay sucursales, regresamos 0
        if (sucursales.isEmpty()) return 0

        // 1. Buscamos específicamente si existe la sucursal con ID = 1
        // (find devuelve el objeto si lo encuentra, o null si no)
        val sucursalUno = sucursales.find { it.id_sucursal == 1 }

        return if (sucursalUno != null) {
            Log.d("SessionManager", "🚀 Se encontró la Sucursal ID 1. Seleccionada.")
            1
        } else {
            // 2. Si NO tiene la sucursal 1 (o es un usuario normal de otra sucursal),
            // tomamos la primera de la lista por defecto.
            Log.d("SessionManager", "⚠️ No tiene la Sucursal 1. Usando la primera: ${sucursales[0].id_sucursal}")
            sucursales[0].id_sucursal
        }
    }

    /**
     * Verifica si el usuario es Admin.
     * Usamos el booleano que ya guardaste en saveLoginData para ser más eficientes.
     */
    fun isAdmin(): Boolean {
        // Corrección: Leemos el valor directo que guardaste con la clave KEY_IS_ADMIN
        return sharedPreferences.getBoolean(KEY_IS_ADMIN, false)
    }
}



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


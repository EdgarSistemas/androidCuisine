package com.intellisoft.androidcuisine.data.remote.api

import android.util.Log
import com.intellisoft.androidcuisine.data.remote.dto.LoginRequest
import com.intellisoft.androidcuisine.data.remote.dto.LoginResponse
import com.intellisoft.androidcuisine.data.remote.interceptor.AuthInterceptor
import com.intellisoft.androidcuisine.data.remote.interceptor.ErrorInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

object ApiClient {
    private const val BASE_URL = "http://10.74.241.162:5000/api/"

    private var context: android.content.Context? = null

    /**
     * Inicializa ApiClient con el contexto de la aplicación
     */
    fun init(applicationContext: android.content.Context) {
        this.context = applicationContext
    }

    private val httpClient: OkHttpClient by lazy {
        val appContext = context ?: throw IllegalStateException("ApiClient no ha sido inicializado. Llama ApiClient.init(context) primero.")

        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor(AuthInterceptor(appContext))
            .addInterceptor(ErrorInterceptor())
            .addInterceptor { chain ->
                val request = chain.request()
                Log.d("APICLIENT", "📡 URL: ${request.url}")
                Log.d("APICLIENT", "📋 Headers: ${request.headers}")
                chain.proceed(request)
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authService: AuthService by lazy {
        retrofit.create(AuthService::class.java)
    }

    val sucursalService: SucursalService by lazy {
        retrofit.create(SucursalService::class.java)
    }
}

interface AuthService {
    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>
}

interface SucursalService {
    @GET("sucursales/activas")
    suspend fun getSucursalesActivas(): Response<com.intellisoft.androidcuisine.data.remote.dto.SucursalResponse>

    @POST("sucursales")
    suspend fun createSucursal(@Body sucursal: com.intellisoft.androidcuisine.data.remote.dto.SucursalRequest): Response<com.intellisoft.androidcuisine.data.remote.dto.SucursalSingleResponse>

    @PUT("sucursales/{id}")
    suspend fun updateSucursal(@Path("id") id: Int, @Body sucursal: com.intellisoft.androidcuisine.data.remote.dto.SucursalRequest): Response<com.intellisoft.androidcuisine.data.remote.dto.SucursalSingleResponse>

    @DELETE("sucursales/{id}")
    suspend fun deleteSucursal(@Path("id") id: Int): Response<com.intellisoft.androidcuisine.data.remote.dto.SucursalSingleResponse>

    @GET("sucursales/{id}")
    suspend fun getSucursal(@Path("id") id: Int): Response<com.intellisoft.androidcuisine.data.remote.dto.SucursalSingleResponse>
}


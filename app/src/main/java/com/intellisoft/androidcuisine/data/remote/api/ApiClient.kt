package com.intellisoft.androidcuisine.data.remote.api

import android.content.Context
import android.util.Log
import com.intellisoft.androidcuisine.data.remote.interceptor.AuthInterceptor
import com.intellisoft.androidcuisine.data.remote.interceptor.ErrorInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private const val BASE_URL = "https://api-cuisine-intellisoft-a2bpbafygvekfcc5.canadacentral-01.azurewebsites.net/api/"

    private var context: Context? = null

    fun init(applicationContext: Context) {
        this.context = applicationContext
    }

    private val httpClient: OkHttpClient by lazy {
        val appContext = context
            ?: throw IllegalStateException("ApiClient no ha sido inicializado. Llama ApiClient.init(context) primero.")

        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor(AuthInterceptor(appContext))
            .addInterceptor(ErrorInterceptor())
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

    val horarioService: HorarioService by lazy {
        retrofit.create(HorarioService::class.java)
    }

    val solicitarService: SolicitarService by lazy {
        retrofit.create(SolicitarService::class.java)
    }

    val usuarioService: UsuarioService by lazy {
        retrofit.create(UsuarioService::class.java)
    }
}
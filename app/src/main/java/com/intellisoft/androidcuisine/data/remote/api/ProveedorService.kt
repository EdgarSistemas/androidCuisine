package com.intellisoft.androidcuisine.data.remote.api

import com.intellisoft.androidcuisine.data.remote.dto.ProveedoresResponse
import retrofit2.Response
import retrofit2.http.GET

interface ProveedorService {
    @GET("proveedores")
    suspend fun getProveedores(): Response<ProveedoresResponse>
}
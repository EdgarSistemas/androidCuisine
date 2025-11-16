package com.intellisoft.androidcuisine.data.remote.api

import com.intellisoft.androidcuisine.data.remote.dto.SucursalRequest
import com.intellisoft.androidcuisine.data.remote.dto.SucursalResponse
import com.intellisoft.androidcuisine.data.remote.dto.SucursalSingleResponse
import retrofit2.Response
import retrofit2.http.*

interface SucursalService {
    @GET("sucursales/activas")
    suspend fun getSucursalesActivas(): Response<SucursalResponse>

    @POST("sucursales")
    suspend fun createSucursal(
        @Body sucursal: SucursalRequest
    ): Response<SucursalSingleResponse>

    @PUT("sucursales/{id}")
    suspend fun updateSucursal(
        @Path("id") id: Int,
        @Body sucursal: SucursalRequest
    ): Response<SucursalSingleResponse>

    @DELETE("sucursales/{id}")
    suspend fun deleteSucursal(@Path("id") id: Int): Response<SucursalSingleResponse>

    @GET("sucursales/{id}")
    suspend fun getSucursal(@Path("id") id: Int): Response<SucursalSingleResponse>
}
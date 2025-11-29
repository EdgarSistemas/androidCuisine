package com.intellisoft.androidcuisine.data.remote.api

import com.intellisoft.androidcuisine.data.remote.dto.InsumosExistenciasRequest
import com.intellisoft.androidcuisine.data.remote.dto.InsumosExistenciasResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface InsumoService {
    @POST("insumos/existencias")
    suspend fun getInsumosExistencias(@Body request: InsumosExistenciasRequest): Response<InsumosExistenciasResponse>
}
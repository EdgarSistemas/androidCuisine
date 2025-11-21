package com.intellisoft.androidcuisine.data.remote.api

import com.intellisoft.androidcuisine.data.remote.dto.MejoraRequest
import com.intellisoft.androidcuisine.data.remote.dto.MejoraSingleResponse
import com.intellisoft.androidcuisine.data.remote.dto.MejorasListResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface MejorasService {
    // POST devuelve un solo objeto en 'data'
    @POST("mejoras")
    suspend fun enviarMejora(@Body request: MejoraRequest): Response<MejoraSingleResponse>

    // GET devuelve una lista en 'data'
    @GET("mejoras")
    suspend fun getMejoras(
        @Query("estatus") estatus: Int? = null
    ): Response<MejorasListResponse>
}
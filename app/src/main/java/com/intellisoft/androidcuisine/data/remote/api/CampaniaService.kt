package com.intellisoft.androidcuisine.data.remote.api

import com.intellisoft.androidcuisine.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface CampaniaService {
    // Campañas
    @GET("campanias")
    suspend fun getCampanias(): Response<CampaniasResponse>

    @GET("campanias/{campania_id}")
    suspend fun getCampania(@Path("campania_id") id: Int): Response<CampaniaDetalleResponse>

    @POST("campanias")
    suspend fun crearCampania(@Body request: CrearCampaniaRequest): Response<CrearCampaniaResponse>

    @POST("campanias/{campania_id}/activar")
    suspend fun activarCampania(@Path("campania_id") id: Int): Response<ActivarDesactivarResponse>

    @POST("campanias/{campania_id}/desactivar")
    suspend fun desactivarCampania(@Path("campania_id") id: Int): Response<ActivarDesactivarResponse>

    // Métricas - Todos son POST con body
    @POST("campanias/metricas/clientes-vip")
    suspend fun getClientesVIP(@Body request: MetricaTopNRequest): Response<ClientesVIPResponse>

    @POST("campanias/metricas/frecuentes")
    suspend fun getClientesFrecuentes(@Body request: MetricaTopNRequest): Response<ClientesFrecuentesResponse>

    @POST("campanias/metricas/inactivos")
    suspend fun getClientesInactivos(@Body request: MetricaDiasSinComprarRequest): Response<ClientesInactivosResponse>

    @POST("campanias/metricas/nuevos")
    suspend fun getClientesNuevos(@Body request: MetricaDiasRegistroRequest): Response<ClientesNuevosResponse>

    @POST("campanias/metricas/por-canal")
    suspend fun getClientesPorCanal(): Response<ClientesPorCanalResponse>

    // Generar campaña desde métrica
    @POST("campanias/generar-desde-metrica")
    suspend fun generarCampaniaDesdeMetrica(@Body request: GenerarCampaniaMetricaRequest): Response<GenerarCampaniaMetricaResponse>
}
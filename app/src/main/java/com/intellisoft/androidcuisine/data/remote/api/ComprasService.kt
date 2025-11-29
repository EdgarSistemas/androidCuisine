package com.intellisoft.androidcuisine.data.remote.api

import com.intellisoft.androidcuisine.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface ComprasService {
    @POST("compras")
    suspend fun crearCompra(@Body compra: CompraRequest): Response<CompraResponse>

    @GET("compras/{sucursalId}")
    suspend fun listarCompras(@Path("sucursalId") sucursalId: Int): Response<ComprasListResponse>

    @GET("compras/id/{compraId}")
    suspend fun obtenerCompra(@Path("compraId") compraId: Int): Response<CompraDetalleResponse>

    @PATCH("compras/id/{compraId}/cancelar")
    suspend fun cancelarCompra(@Path("compraId") compraId: Int): Response<CancelarCompraResponse>
}
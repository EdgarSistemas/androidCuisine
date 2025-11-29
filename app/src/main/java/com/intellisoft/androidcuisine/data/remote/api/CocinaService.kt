package com.intellisoft.androidcuisine.data.remote.api

import com.intellisoft.androidcuisine.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface CocinaService {

    @POST("cocina/pendientes")
    suspend fun getItemsPendientes(@Body request: CocinaSucursalRequest): Response<ItemsPendientesResponse>

    @PUT("cocina/item/{item_id}/listo")
    suspend fun marcarItemListo(@Path("item_id") itemId: Int): Response<MarcarListoResponse>

    @POST("cocina/pedidos")
    suspend fun getPedidosEnCocina(@Body request: CocinaSucursalRequest): Response<PedidosCocinaResponse>
}
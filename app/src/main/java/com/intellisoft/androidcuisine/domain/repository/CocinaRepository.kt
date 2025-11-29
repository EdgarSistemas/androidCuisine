package com.intellisoft.androidcuisine.domain.repository

import com.intellisoft.androidcuisine.data.remote.dto.*

interface CocinaRepository {
    suspend fun getItemsPendientes(sucursalId: Int?): Result<List<ItemPendienteDto>>
    suspend fun marcarItemListo(itemId: Int): Result<MarcarListoResponse>
    suspend fun getPedidosEnCocina(sucursalId: Int?): Result<List<PedidoCocinaDto>>
}
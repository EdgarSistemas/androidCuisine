package com.intellisoft.androidcuisine.domain.repository.cocina

import com.intellisoft.androidcuisine.data.remote.api.ApiClient
import com.intellisoft.androidcuisine.data.remote.dto.*

class CocinaRepositoryImpl : CocinaRepository {

    override suspend fun getItemsPendientes(sucursalId: Int?): Result<List<ItemPendienteDto>> {
        return try {
            val request = CocinaSucursalRequest(sucursal_id = sucursalId)
            val response = ApiClient.cocinaService.getItemsPendientes(request)

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                Result.success(body.items ?: emptyList())
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error desconocido"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun marcarItemListo(itemId: Int): Result<MarcarListoResponse> {
        return try {
            val response = ApiClient.cocinaService.marcarItemListo(itemId)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error desconocido"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPedidosEnCocina(sucursalId: Int?): Result<List<PedidoCocinaDto>> {
        return try {
            val request = CocinaSucursalRequest(sucursal_id = sucursalId)
            val response = ApiClient.cocinaService.getPedidosEnCocina(request)

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                Result.success(body.pedidos ?: emptyList())
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error desconocido"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
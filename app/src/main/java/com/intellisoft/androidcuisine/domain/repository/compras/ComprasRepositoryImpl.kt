package com.intellisoft.androidcuisine.domain.repository.compras

import com.intellisoft.androidcuisine.data.remote.api.ApiClient
import com.intellisoft.androidcuisine.data.remote.dto.*

class ComprasRepositoryImpl : ComprasRepository {

    override suspend fun crearCompra(compra: CompraRequest): Result<CompraData> {
        return try {
            val response = ApiClient.comprasService.crearCompra(compra)

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success && body.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body.message ?: "Error al crear compra"))
                }
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error desconocido"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun listarCompras(sucursalId: Int): Result<List<CompraData>> {
        return try {
            val response = ApiClient.comprasService.listarCompras(sucursalId)

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) {
                    Result.success(body.data ?: emptyList())
                } else {
                    Result.failure(Exception(body.message ?: "Error al listar compras"))
                }
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error desconocido"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun obtenerCompra(compraId: Int): Result<CompraDetalleCompleta> {
        return try {
            val response = ApiClient.comprasService.obtenerCompra(compraId)

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success && body.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body.message ?: "Error al obtener compra"))
                }
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error desconocido"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun cancelarCompra(compraId: Int): Result<String> {
        return try {
            val response = ApiClient.comprasService.cancelarCompra(compraId)

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) {
                    Result.success(body.message)
                } else {
                    Result.failure(Exception(body.message))
                }
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error desconocido"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
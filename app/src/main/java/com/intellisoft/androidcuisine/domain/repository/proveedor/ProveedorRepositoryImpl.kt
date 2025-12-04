package com.intellisoft.androidcuisine.domain.repository.proveedor

import com.intellisoft.androidcuisine.data.remote.api.ApiClient
import com.intellisoft.androidcuisine.data.remote.dto.ProveedorDto

class ProveedorRepositoryImpl : ProveedorRepository {

    override suspend fun getProveedores(): Result<List<ProveedorDto>> {
        return try {
            val response = ApiClient.proveedorService.getProveedores()

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) {
                    Result.success(body.data ?: emptyList())
                } else {
                    Result.failure(Exception(body.message ?: "Error al obtener proveedores"))
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
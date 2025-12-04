package com.intellisoft.androidcuisine.domain.repository.campania

import com.intellisoft.androidcuisine.data.remote.api.ApiClient
import com.intellisoft.androidcuisine.data.remote.dto.*

class CampaniaRepositoryImpl : CampaniaRepository {

    override suspend fun getCampanias(): Result<List<CampaniaDto>> {
        return try {
            val response = ApiClient.campaniaService.getCampanias()

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                Result.success(body.campanias ?: emptyList())
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error desconocido"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCampania(id: Int): Result<CampaniaDto> {
        return try {
            val response = ApiClient.campaniaService.getCampania(id)

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success && body.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body.message ?: "Error al obtener campaña"))
                }
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error desconocido"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun crearCampania(request: CrearCampaniaRequest): Result<CampaniaDto> {
        return try {
            val response = ApiClient.campaniaService.crearCampania(request)

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success && body.data != null) {
                    Result.success(body.data)
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

    override suspend fun activarCampania(id: Int): Result<String> {
        return try {
            val response = ApiClient.campaniaService.activarCampania(id)

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

    override suspend fun desactivarCampania(id: Int): Result<String> {
        return try {
            val response = ApiClient.campaniaService.desactivarCampania(id)

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

    override suspend fun getClientesVIP(topN: Int): Result<List<ClienteVIPDto>> {
        return try {
            val request = MetricaTopNRequest(top_n = topN)
            val response = ApiClient.campaniaService.getClientesVIP(request)

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                Result.success(body.clientes ?: emptyList())
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error desconocido"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getClientesFrecuentes(topN: Int): Result<List<ClienteFrecuenteDto>> {
        return try {
            val request = MetricaTopNRequest(top_n = topN)
            val response = ApiClient.campaniaService.getClientesFrecuentes(request)

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                Result.success(body.clientes ?: emptyList())
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error desconocido"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getClientesInactivos(dias: Int): Result<List<ClienteInactivoDto>> {
        return try {
            val request = MetricaDiasSinComprarRequest(dias_sin_comprar = dias)
            val response = ApiClient.campaniaService.getClientesInactivos(request)

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                Result.success(body.clientes ?: emptyList())
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error desconocido"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getClientesNuevos(dias: Int): Result<List<ClienteNuevoDto>> {
        return try {
            val request = MetricaDiasRegistroRequest(dias_registro = dias)
            val response = ApiClient.campaniaService.getClientesNuevos(request)

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                Result.success(body.clientes ?: emptyList())
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error desconocido"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getClientesPorCanal(): Result<List<ClienteCanalDto>> {
        return try {
            val response = ApiClient.campaniaService.getClientesPorCanal()

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                Result.success(body.clientes ?: emptyList())
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error desconocido"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun generarCampaniaDesdeMetrica(request: GenerarCampaniaMetricaRequest): Result<GenerarCampaniaMetricaResponse> {
        return try {
            val response = ApiClient.campaniaService.generarCampaniaDesdeMetrica(request)

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                Result.success(body)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error desconocido"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
package com.intellisoft.androidcuisine.domain.repository.recuperacion

import com.intellisoft.androidcuisine.data.remote.api.ApiClient
import com.intellisoft.androidcuisine.data.remote.dto.SolicitarRequest
import com.intellisoft.androidcuisine.data.remote.dto.VerificacionYSolicitudResponse
import com.intellisoft.androidcuisine.data.remote.dto.VerificarCodigoRequest
import com.intellisoft.androidcuisine.data.remote.dto.restablecerRequest
import kotlin.String

//Se tienen que sobreescribir los metodos de la interfaz
class recuperacionRepositoryImpl : recuperacionRepository {

    override suspend fun postSolicitarCodigo(email: String): Result<Boolean>{
        return try {
            val response = ApiClient.solicitarService.crearSolicitud(SolicitarRequest(email))
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success && body.message != null) {
                    Result.success(body.success)
                } else {
                    Result.failure(Exception(body.message ?: "Error al solicitar el codigo"))
                }
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error desconocido"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception){
            Result.failure(e)
        }
    }

    override suspend fun postVerificarSolicitud(codigo: String, email: String): Result<String>{
        return try {
            val response = ApiClient.solicitarService.verficiarSolicitud(VerificarCodigoRequest(codigo, email))

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success && body.message != null) {
                    Result.success(body.message)
                } else {
                    Result.failure(Exception(body.message ?: "Error al verificar la solicitud"))
                }
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error desconocido"
                Result.failure(Exception(errorMsg))
            }

        } catch (e: Exception){
            Result.failure(e)        }
    }

    override suspend fun postRestablecerSolicitud(codigo: String, email: String, password: String): Result<VerificacionYSolicitudResponse>{
        return try {
            val response = ApiClient.solicitarService.restablecerSolicitud(restablecerRequest(codigo, email, password))
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success && body.message != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception(body.message ?: "Error al restablecer la contraseña"))
                }
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error desconocido"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception){
            Result.failure(e)
        }
    }
}
package com.intellisoft.androidcuisine.domain.repository.recuperacion

import com.intellisoft.androidcuisine.data.remote.dto.VerificacionYSolicitudResponse

interface recuperacionRepository {
    //las interfaces se usan para agrupar los metodos compartidos en varias clases

    suspend fun postSolicitarCodigo(email: String): Result<Boolean>

    suspend fun postVerificarSolicitud(codigo: String, email: String): Result<String>

    suspend fun postRestablecerSolicitud(codigo: String, email: String, password: String): Result<VerificacionYSolicitudResponse>
}
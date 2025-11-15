package com.intellisoft.androidcuisine.domain.repository

import com.intellisoft.androidcuisine.data.remote.dto.HorarioUsuarioData

interface HorarioRepository {
    suspend fun getHorarioUsuario(userId: Int): Result<HorarioUsuarioData>
}
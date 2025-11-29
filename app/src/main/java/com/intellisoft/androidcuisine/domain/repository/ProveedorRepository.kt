package com.intellisoft.androidcuisine.domain.repository

import com.intellisoft.androidcuisine.data.remote.dto.ProveedorDto

interface ProveedorRepository {
    suspend fun getProveedores(): Result<List<ProveedorDto>>
}
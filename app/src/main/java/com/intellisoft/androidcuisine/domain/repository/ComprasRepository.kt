package com.intellisoft.androidcuisine.domain.repository

import com.intellisoft.androidcuisine.data.remote.dto.*

interface ComprasRepository {
    suspend fun crearCompra(compra: CompraRequest): Result<CompraData>
    suspend fun listarCompras(sucursalId: Int): Result<List<CompraData>>
    suspend fun obtenerCompra(compraId: Int): Result<CompraDetalleCompleta>
    suspend fun cancelarCompra(compraId: Int): Result<String>
}
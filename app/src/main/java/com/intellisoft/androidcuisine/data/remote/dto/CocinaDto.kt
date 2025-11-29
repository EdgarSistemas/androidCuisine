package com.intellisoft.androidcuisine.data.remote.dto

// Request
data class CocinaSucursalRequest(
    val sucursal_id: Int? = null
)

// Response Items Pendientes
data class ItemsPendientesResponse(
    val items: List<ItemPendienteDto>? = null,
    val total: Int? = null,
    val success: Boolean? = null,
    val message: String? = null
)

data class ItemPendienteDto(
    val id_pedido_item: Int,
    val pedido_id: Int,
    val folio_pedido: String,
    val tipo_pedido: Int,
    val tipo_pedido_display: String,
    val mesa_id: Int? = null,
    val producto_id: Int? = null,
    val combo_id: Int? = null,
    val producto_nombre: String,
    val cantidad: Int,
    val notas: String? = null,
    val estatus_detalle: Int,
    val created_at: String,
    val tiempo_en_cocina: String
)

// Response Marcar Listo
data class MarcarListoResponse(
    val message: String? = null,
    val item: ItemListoDto? = null,
    val pedido_auto_pagado: Boolean = false,
    val success: Boolean? = null
)

data class ItemListoDto(
    val id_pedido_item: Int,
    val pedido_id: Int,
    val estatus_detalle: Int,
    val producto_id: Int? = null,
    val cantidad: Int
)

// Response Pedidos en Cocina
data class PedidosCocinaResponse(
    val pedidos: List<PedidoCocinaDto>? = null,
    val total: Int? = null,
    val success: Boolean? = null,
    val message: String? = null
)

data class PedidoCocinaDto(
    val id_pedido: Int,
    val folio: String,
    val tipo_pedido: Int,
    val tipo_pedido_display: String,
    val mesa_id: Int? = null,
    val estado_pedido: Int,
    val created_at: String,
    val progreso: String,
    val items_en_cocina: List<ItemEnCocinaDto>? = null
)

data class ItemEnCocinaDto(
    val id_pedido_item: Int,
    val producto_id: Int? = null,
    val combo_id: Int? = null,
    val producto_nombre: String? = null,
    val cantidad: Int,
    val notas: String? = null,
    val created_at: String
)
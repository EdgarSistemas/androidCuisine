package com.intellisoft.androidcuisine.views.compras

data class ArticuloCompra(
    val insumoId: Int,
    val nombreInsumo: String,
    val cantidad: Double,
    val presentacion: String,
    val costoUnitario: Double
) {
    val subtotal: Double
        get() = cantidad * costoUnitario
}
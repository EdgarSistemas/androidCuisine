package com.intellisoft.androidcuisine.data.remote.dto

data class CompraRequest(
    val sucursal_id: Int,
    val proveedor_id: Int,
    val detalles: List<CompraDetalleRequest>
)

data class CompraDetalleRequest(
    val insumo_id: Int,
    val cant_presentacion: Double,
    val costo_unit_present: Double,
    val presentacion: String
)

data class CompraResponse(
    val success: Boolean,
    val message: String? = null,
    val data: CompraData? = null
)

data class CompraData(
    val id_compra: Int,
    val usuario_id: Int,
    val sucursal_id: Int,
    val proveedor_id: Int,
    val folio: String,
    val fecha_compra: String,
    val estatus: Int,
    val created_at: String,
    val updated_at: String?,
    val detalles: List<CompraDetalleData>? = null
)

data class CompraDetalleData(
    val id_compra_detalle: Int,
    val compra_id: Int,
    val insumo_id: Int,
    val cant_presentacion: Double,
    val costo_unit_present: Double,
    val presentacion: String? = null,
    val subtotal: Double? = null,
    val created_at: String,
    val updated_at: String?,
    val insumo: InsumoBasicData? = null
)

data class InsumoBasicData(
    val id_insumo: Int,
    val nombre: String,
    val unidad_medida: UnidadMedidaData? = null
)

data class UnidadMedidaData(
    val id_unidad_medida: Int,
    val clave: String,
    val nombre: String
)

data class ComprasListResponse(
    val success: Boolean,
    val data: List<CompraData>? = null,
    val total: Int? = null,
    val message: String? = null
)

data class CompraDetalleResponse(
    val success: Boolean,
    val data: CompraDetalleCompleta? = null,
    val message: String? = null
)

data class CompraDetalleCompleta(
    val id_compra: Int,
    val folio: String,
    val fecha_compra: String,
    val estatus: Int,
    val total_compra: Double,
    val created_at: String,
    val updated_at: String?,
    val proveedor: ProveedorBasicData,
    val sucursal: SucursalBasicData,
    val usuario: UsuarioBasicData,
    val detalles: List<CompraDetalleData>
)

data class ProveedorBasicData(
    val id_proveedor: Int,
    val nombre: String,
    val telefono: String?,
    val email: String?
)

data class SucursalBasicData(
    val id_sucursal: Int,
    val nombre: String,
    val codigo_sucursal: String
)

data class UsuarioBasicData(
    val id_usuario: Int,
    val nombre: String,
    val apellido: String,
    val email: String
)

data class CancelarCompraResponse(
    val success: Boolean,
    val message: String
)
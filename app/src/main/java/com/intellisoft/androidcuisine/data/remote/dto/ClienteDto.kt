package com.intellisoft.androidcuisine.data.remote.dto

// ==================== SUCURSALES ====================

data class SucursalesActivasResponse(
    val success: Boolean,
    val data: List<SucursalActivaDto>
)

data class SucursalActivaDto(
    val id_sucursal: Int,
    val codigo_sucursal: String,
    val nombre: String,
    val telefono: String?,
    val direccion: String?,
    val es_activa: Boolean
)

// ==================== ÁREAS ====================

data class AreasClienteResponse(
    val success: Boolean,
    val data: List<AreaClienteDto>,
    val total: Int
)

data class AreaClienteDto(
    val id_area: Int,
    val sucursal_id: Int,
    val nombre: String,
    val descripcion: String?,
    val es_activa: Boolean
)

// ==================== MESAS (Cliente) ====================

data class MesasClienteResponse(
    val success: Boolean,
    val data: List<MesaClienteDto>,
    val total: Int
)

data class MesaClienteDto(
    val id_mesa: Int,
    val area_id: Int,
    val codigo_mesa: String,
    val capacidad: Int,
    val es_activa: Boolean,
    val estatus_actual: Int,
    val estatus_display: String
)

// ==================== MENÚ - CATEGORÍAS ====================

data class CategoriasResponse(
    val success: Boolean,
    val data: List<CategoriaDto>
)

data class CategoriaDto(
    val id_categoria: Int,
    val nombre: String,
    val descripcion: String?,
    val es_activa: Boolean
)

// ==================== MENÚ - PRODUCTOS ====================

data class ProductosResponse(
    val success: Boolean,
    val data: List<ProductoDto>
)

data class ProductoDto(
    val id_producto: Int,
    val categoria_id: Int,
    val codigo: String?,
    val nombre: String,
    val descripcion: String?,
    val imagen_url: String?,
    val precio: Double,
    val es_activo: Boolean
)

// ==================== MENÚ - COMBOS ====================

data class CombosResponse(
    val success: Boolean,
    val data: List<ComboDto>
)

data class ComboDto(
    val id_combo: Int,
    val nombre: String,
    val descripcion: String?,
    val imagen_url: String?,
    val precio: Double,
    val es_activo: Boolean,
    val productos: List<ComboProductoDto>?
)

data class ComboProductoDto(
    val producto_id: Int,
    val nombre: String,
    val cantidad: Int
)

// ==================== RESERVAS (Cliente) ====================
// Nota: Los DTOs de Hold (CrearHoldRequest, HoldDto, etc.) están en ReservasDto.kt

data class AccionReservaClienteResponse(
    val message: String? = null,
    val reserva: ReservaClienteDto? = null
)

data class CrearReservaClienteRequest(
    val hold_id: Int,
    val cliente_id: Int?,  // <-- AGREGAR ESTO
    val inicio: String,
    val fin_estimado: String,
    val tolerancia_min: Int = 10,
    val notas: String? = null
)

data class CrearReservaClienteResponse(
    val message: String?,
    val reserva: ReservaClienteDto?
)

data class ReservaClienteDto(
    val id_reserva: Int,
    val cliente_id: Int?,
    val inicio: String,
    val fin_estimado: String,
    val estatus: Int,
    val estatus_display: String?,
    val tolerancia_min: Int,
    val notas: String?,
    val hold_id: Int?,
    val puede_iniciar: Boolean?,
    val created_at: String?
)

data class ListarReservasClienteRequest(
    val cliente_id: Int? = null,
    val sucursal_id: Int? = null,
    val estatus: Int? = null,
    val fecha_desde: String? = null,
    val fecha_hasta: String? = null
)

data class ListarReservasClienteResponse(
    val reservas: List<ReservaClienteDto>?,
    val total: Int?
)

data class CancelarReservaResponse(
    val success: Boolean,
    val data: ReservaClienteDto
)

// ==================== PEDIDOS ====================

data class ItemPedidoRequest(
    val producto_id: Int? = null,
    val combo_id: Int? = null,
    val cantidad: Int,
    val notas: String? = null
)

data class CrearPedidoDineInRequest(
    val sucursal_id: Int,
    val cliente_id: Int,
    val tipo_pedido: Int = 1,  // ← AGREGAR ESTA LÍNEA
    val canal: Int = 2,
    val reserva_id: Int,
    val notas: String? = null,
    val items: List<ItemPedidoRequest>? = null
)

data class CrearPedidoTakeawayRequest(
    val sucursal_id: Int,
    val cliente_id: Int,
    val canal: Int = 2,
    val notas: String? = null,
    val items: List<ItemPedidoRequest>? = null
)

data class CrearPedidoResponse(
    val mensaje: String,
    val pedido: PedidoClienteDto
)

data class PedidoClienteDto(
    val id_pedido: Int,
    val sucursal_id: Int,
    val cliente_id: Int?,
    val tipo_pedido: Int,
    val tipo_pedido_display: String?,
    val canal: Int?,
    val estado_pedido: Int,
    val estado_display: String?,
    val reserva_id: Int?,
    val mesa_id: Int?,
    val total: Double,
    val notas: String?,
    val items: List<ItemPedidoDto>?,
    val created_at: String?
)

data class ItemPedidoDto(
    val id_pedido_item: Int,
    val producto_id: Int?,
    val producto_nombre: String?,
    val combo_id: Int?,
    val combo_nombre: String?,
    val cantidad: Int,
    val precio_unit: Double,
    val subtotal: Double,
    val estatus_detalle: Int,
    val estatus_display: String?,
    val notas: String?,
    val consumo_exitoso: Boolean?
)

data class AgregarItemResponse(
    val message: String,
    val item: ItemPedidoDto
)

data class ListarPedidosRequest(
    val cliente_id: Int? = null,
    val sucursal_id: Int? = null,
    val estado: Int? = null,
    val tipo_pedido: Int? = null,
    val fecha_desde: String? = null,
    val fecha_hasta: String? = null
)

data class ListarPedidosResponse(
    val pedidos: List<PedidoClienteDto>?,
    val total: Int?
)

data class AccionPedidoRequest(
    val comentario: String? = null
)

data class AccionPedidoResponse(
    val mensaje: String,
    val pedido: PedidoClienteDto
)

// ==================== CUPONES ====================

data class MisCuponesResponse(
    val cupones: List<CuponClienteDto>,
    val total: Int
)

data class CuponClienteDto(
    val id_campania_usuario: Int,
    val campania_id: Int,
    val nombre_campania: String,
    val codigo: String,
    val porcentaje_desc: Double,
    val fecha_vigencia: String,
    val usado: Boolean
)

data class ValidarCuponRequest(
    val codigo: String,
    val cliente_id: Int
)

data class ValidarCuponResponse(
    val valido: Boolean,
    val error: String?,
    val campania: CampaniaInfoDto?,
    val porcentaje_desc: Double?,
    val campania_usuario_id: Int?
)

data class CampaniaInfoDto(
    val id_campania: Int,
    val nombre_campania: String
)

// ==================== PAGOS ====================

data class CrearPagoRequest(
    val pedido_id: Int,
    val sucursal_id: Int,
    val monto: Double,
    val propina: Double = 0.0,
    val moneda: String = "MXN",
    val campania_usuario_id: Int? = null,
    val monto_descontado: Double? = null
)

data class CrearPagoResponse(
    val success: Boolean,
    val data: PagoDto
)

data class PagoDto(
    val id_pago: Int,
    val pedido_id: Int,
    val monto: Double,
    val propina: Double,
    val monto_descontado: Double?,
    val total: Double,
    val moneda: String,
    val created_at: String
)

// ==================== CALIFICACIONES ====================

data class CrearCalificacionRequest(
    val pedido_id: Int,
    val empleado_id: Int? = null,
    val calificacion: Int,
    val notas: String? = null
)

data class CalificacionResponse(
    val success: Boolean,
    val data: CalificacionDto
)

data class CalificacionDto(
    val id_calificacion: Int,
    val pedido_id: Int,
    val cliente_id: Int?,
    val empleado_id: Int?,
    val calificacion: Int,
    val notas: String?,
    val created_at: String
)

data class ListarCalificacionesResponse(
    val success: Boolean,
    val data: List<CalificacionDto>
)

// ==================== MEJORAS ====================

data class CrearMejoraRequest(
    val notas: String
)

data class MejoraResponse(
    val success: Boolean,
    val data: MejoraDto
)

data class MejoraDto(
    val id_mejora: Int,
    val cliente_id: Int,
    val notas: String,
    val estatus: Int,
    val created_at: String
)

data class ListarMejorasResponse(
    val success: Boolean,
    val data: List<MejoraDto>
)
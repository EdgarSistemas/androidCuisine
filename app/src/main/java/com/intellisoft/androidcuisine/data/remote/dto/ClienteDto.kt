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

data class SucursalDetalleResponse(
    val success: Boolean,
    val data: SucursalActivaDto
)

// ==================== ÁREAS ====================

data class AreasResponse(
    val success: Boolean,
    val data: List<AreaDto>,
    val total: Int? = null
)

data class AreaDto(
    val id_area: Int,
    val sucursal_id: Int,
    val nombre: String,
    val descripcion: String?,
    val es_activa: Boolean
)

// ==================== MESAS (para cliente) ====================

data class MesasClienteResponse(
    val success: Boolean,
    val data: List<MesaClienteDto>,
    val total: Int? = null
)

data class MesaClienteDto(
    val id_mesa: Int,
    val area_id: Int?,
    val codigo_mesa: String,
    val capacidad: Int,
    val es_activa: Boolean,
    val estatus_actual: Int,
    val estatus_display: String?
)

// ==================== HOLDS (Cliente) ====================

data class CrearHoldClienteRequest(
    val mesa_id: Int,
    val inicio: String,
    val fin: String
)

data class CrearHoldClienteResponse(
    val mensaje: String,
    val hold: HoldClienteDto
)

data class HoldClienteDto(
    val id_hold: Int,
    val mesa_id: Int,
    val expira_at: String,
    val ttl_segundos: Int
)

// ==================== RESERVAS (Cliente) ====================

data class CrearReservaClienteRequest(
    val cliente_id: Int,
    val hold_id: Int,
    val inicio: String,
    val fin_estimado: String,
    val tolerancia_min: Int = 15,
    val notas: String? = null
)

data class CrearReservaClienteResponse(
    val mensaje: String,
    val reserva: ReservaClienteDto
)

data class ReservaClienteDto(
    val id_reserva: Int,
    val mesa_id: Int,
    val cliente_id: Int,
    val inicio: String,
    val fin_estimado: String,
    val estado: Int,
    val estado_descripcion: String? = null,
    val tolerancia_min: Int,
    val notas: String? = null,
    val codigo_mesa: String? = null,
    val sucursal_nombre: String? = null
)

data class ListarReservasClienteRequest(
    val cliente_id: Int,
    val estado: Int? = null,
    val fecha_desde: String? = null,
    val fecha_hasta: String? = null
)

data class ListarReservasClienteResponse(
    val reservas: List<ReservaClienteDto>,
    val total: Int? = null,
    val success: Boolean? = true
)

data class CancelarReservaClienteRequest(
    val motivo: String? = null
)

data class CancelarReservaClienteResponse(
    val mensaje: String,
    val reserva: ReservaClienteDto? = null,
    val success: Boolean? = true
)

// ==================== PEDIDOS (Cliente) ====================

data class CrearPedidoDineInRequest(
    val sucursal_id: Int,
    val cliente_id: Int,
    val reserva_id: Int,
    val items: List<ItemPedidoRequest>,
    val canal: String = "App",
    val notas: String? = null
)

data class CrearPedidoTakeawayRequest(
    val sucursal_id: Int,
    val cliente_id: Int,
    val items: List<ItemPedidoRequest>,
    val canal: String = "App",
    val notas: String? = null
)

data class ItemPedidoRequest(
    val producto_id: Int? = null,
    val combo_id: Int? = null,
    val cantidad: Int,
    val notas: String? = null
)

data class AgregarItemRequest(
    val producto_id: Int? = null,
    val combo_id: Int? = null,
    val cantidad: Int,
    val notas: String? = null
)

data class CrearPedidoResponse(
    val mensaje: String,
    val pedido: PedidoCreadoDto
)

data class PedidoCreadoDto(
    val id_pedido: Int,
    val tipo_pedido: Int,
    val estado: Int,
    val items_creados: Int,
    val total_estimado: Double? = null
)

data class PedidoDetalleResponse(
    val pedido: PedidoDetalleDto
)

data class PedidoDetalleDto(
    val id_pedido: Int,
    val sucursal_id: Int?,
    val cliente_id: Int?,
    val tipo_pedido: Int,
    val tipo_descripcion: String?,
    val estado: Int,
    val estado_descripcion: String?,
    val items: List<ItemPedidoDetalleDto>?,
    val total: Double?,
    val created_at: String?
)

data class ItemPedidoDetalleDto(
    val id_item: Int,
    val producto: String?,
    val combo: String?,
    val cantidad: Int,
    val estado: Int,
    val estado_descripcion: String?,
    val notas: String?
)

data class ListarPedidosClienteRequest(
    val sucursal_id: Int? = null,
    val cliente_id: Int? = null,
    val estado: Int? = null,
    val tipo_pedido: Int? = null,
    val fecha_desde: String? = null,
    val fecha_hasta: String? = null
)

data class ListarPedidosClienteResponse(
    val pedidos: List<PedidoDetalleDto>,
    val total: Int? = null,
    val success: Boolean? = true
)

data class AgregarItemResponse(
    val mensaje: String,
    val insumos_consumidos: Int?,
    val item: ItemAgregadoDto
)

data class ItemAgregadoDto(
    val id_pedido_item: Int,
    val producto_id: Int?,
    val combo_id: Int?,
    val cantidad: Int,
    val estado: Int
)

data class AccionPedidoRequest(
    val comentario: String? = null
)

data class AccionPedidoResponse(
    val mensaje: String,
    val pedido: PedidoEstadoDto?
)

data class PedidoEstadoDto(
    val id_pedido: Int,
    val estado: Int
)

// ==================== CUPONES ====================

data class MisCuponesResponse(
    val cupones: List<CuponClienteDto>,
    val total: Int? = null
)

data class CuponClienteDto(
    val id: Int,
    val codigo: String,
    val campania: String,
    val porcentaje_desc: Double,
    val fecha_expira: String?,
    val estatus: Int,
    val estatus_descripcion: String
)

data class ValidarCuponRequest(
    val codigo: String,
    val cliente_id: Int
)

data class ValidarCuponResponse(
    val valido: Boolean,
    val campania: CampaniaInfoDto? = null,
    val porcentaje_desc: Double? = null,
    val campania_usuario_id: Int? = null,
    val error: String? = null
)

data class CampaniaInfoDto(
    val nombre: String,
    val descripcion: String?
)

// ==================== CALIFICACIONES ====================

data class CrearCalificacionRequest(
    val pedido_id: Int,
    val empleado_id: Int,
    val calificacion: Int,
    val notas: String? = null
)

data class CalificacionResponse(
    val mensaje: String?,
    val calificacion: CalificacionDto
)

data class CalificacionDto(
    val id_calificacion: Int,
    val pedido_id: Int,
    val empleado_id: Int,
    val calificacion: Int,
    val notas: String?,
    val created_at: String?
)

data class ListarCalificacionesResponse(
    val calificaciones: List<CalificacionDto>,
    val total: Int? = null
)

// ==================== CATEGORÍAS ====================

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

// ==================== PRODUCTOS ====================

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

// ==================== COMBOS ====================

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

// ==================== CARRITO ====================

data class CarritoItem(
    val productoId: Int?,
    val comboId: Int?,
    val nombre: String,
    val precio: Double,
    var cantidad: Int,
    var notas: String?
)
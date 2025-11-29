package com.intellisoft.androidcuisine.data.remote.dto

// Campañas
data class CampaniasResponse(
    val campanias: List<CampaniaDto>? = null,
    val success: Boolean? = null,
    val message: String? = null
)

data class CampaniaDto(
    val id_campania: Int,
    val codigo: String,
    val nombre_campania: String,
    val porcentaje_desc: Double,
    val estatus: Int,
    val total_cupones: Int? = null,
    val total_cupones_usados: Int? = null,
    val usuario_crea_id: Int? = null,
    val created_at: String? = null
)

data class CampaniaDetalleResponse(
    val success: Boolean,
    val data: CampaniaDto? = null,
    val message: String? = null
)

data class CrearCampaniaRequest(
    val nombre_campania: String,
    val porcentaje_desc: Double,
    val codigo: String
)

data class CrearCampaniaResponse(
    val success: Boolean,
    val message: String,
    val data: CampaniaDto? = null
)

data class ActivarDesactivarResponse(
    val success: Boolean,
    val message: String
)

// Métricas - Request con body
data class MetricaTopNRequest(
    val top_n: Int = 20
)

data class MetricaDiasSinComprarRequest(
    val dias_sin_comprar: Int = 30
)

data class MetricaDiasRegistroRequest(
    val dias_registro: Int = 30
)

// Métricas - Responses
data class ClientesVIPResponse(
    val metrica: String? = null,
    val descripcion: String? = null,
    val clientes: List<ClienteVIPDto>? = null,
    val total: Int? = null,
    val success: Boolean? = null,
    val message: String? = null
)

data class ClienteVIPDto(
    val id_usuario: Int,
    val nombre: String? = null,
    val apellido: String? = null,
    val nombre_completo: String,
    val email: String,
    val telefono: String? = null,
    val total_pedidos: Int,
    val gasto_total: Double,
    val propinas_total: Double? = null,
    val ticket_promedio: Double,
    val segmento: String
)

data class ClientesFrecuentesResponse(
    val metrica: String? = null,
    val descripcion: String? = null,
    val clientes: List<ClienteFrecuenteDto>? = null,
    val total: Int? = null,
    val success: Boolean? = null,
    val message: String? = null
)

data class ClienteFrecuenteDto(
    val id_usuario: Int,
    val nombre: String? = null,
    val apellido: String? = null,
    val nombre_completo: String,
    val email: String? = null,
    val telefono: String? = null,
    val total_pedidos: Int,
    val total_reservas: Int,
    val total_interacciones: Int,
    val primera_visita: String,
    val ultima_visita: String,
    val segmento: String
)

data class ClientesInactivosResponse(
    val metrica: String? = null,
    val descripcion: String? = null,
    val clientes: List<ClienteInactivoDto>? = null,
    val total: Int? = null,
    val success: Boolean? = null,
    val message: String? = null
)

data class ClienteInactivoDto(
    val id_usuario: Int,
    val nombre: String? = null,
    val apellido: String? = null,
    val nombre_completo: String,
    val email: String? = null,
    val telefono: String? = null,
    val ultimo_pedido: String,
    val dias_sin_comprar: Int,
    val historico_pedidos: Int,
    val gasto_historico: Double,
    val nivel_riesgo: String,
    val segmento: String
)

data class ClientesNuevosResponse(
    val metrica: String? = null,
    val descripcion: String? = null,
    val clientes: List<ClienteNuevoDto>? = null,
    val total: Int? = null,
    val success: Boolean? = null,
    val message: String? = null
)

data class ClienteNuevoDto(
    val id_usuario: Int,
    val nombre: String? = null,
    val apellido: String? = null,
    val nombre_completo: String,
    val email: String? = null,
    val telefono: String? = null,
    val fecha_registro: String,
    val dias_desde_registro: Int,
    val total_pedidos: Int,
    val gasto_total: Double,
    val etapa: String,
    val segmento: String
)

data class ClientesPorCanalResponse(
    val metrica: String? = null,
    val descripcion: String? = null,
    val clientes: List<ClienteCanalDto>? = null,
    val total: Int? = null,
    val resumen_canales: ResumenCanales? = null,
    val success: Boolean? = null,
    val message: String? = null
)

data class ClienteCanalDto(
    val id_usuario: Int,
    val nombre: String? = null,
    val apellido: String? = null,
    val nombre_completo: String,
    val email: String? = null,
    val telefono: String? = null,
    val pedidos_mesa: Int,
    val pedidos_takeaway: Int,
    val pedidos_delivery: Int,
    val total_reservas: Int,
    val total_pedidos: Int? = null,
    val canal_preferido: String,
    val segmento: String
)

data class ResumenCanales(
    val MESA_PREFERIDO: Int? = null,
    val TAKEAWAY_PREFERIDO: Int? = null,
    val DELIVERY_PREFERIDO: Int? = null,
    val RESERVA_PREFERIDO: Int? = null,
    val MIXTO: Int? = null
)

// Generar campaña desde métrica - NUEVO REQUEST SEGÚN DOCUMENTACIÓN
data class GenerarCampaniaMetricaRequest(
    val nombre_campania: String,
    val porcentaje_desc: Double,
    val codigo: String,
    val cliente_ids: List<Int>,
    val fecha_vigencia: String? = null
)

data class GenerarCampaniaMetricaResponse(
    val message: String? = null,
    val campania: CampaniaDto? = null,
    val asignaciones: AsignacionesInfo? = null,
    val success: Boolean? = null
)

data class AsignacionesInfo(
    val campania_id: Int? = null,
    val total_clientes: Int? = null,
    val asignados: Int? = null,
    val ya_asignados: Int? = null,
    val errores: Int? = null,
    val detalle_errores: List<String>? = null
)
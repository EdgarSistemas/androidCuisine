package com.intellisoft.androidcuisine.domain.repository.cliente

import com.intellisoft.androidcuisine.data.remote.api.ApiClient
import com.intellisoft.androidcuisine.data.remote.dto.*
import com.intellisoft.androidcuisine.domain.repository.cliente.ClienteRepository

class ClienteRepositoryImpl : ClienteRepository {

    // ==================== CATÁLOGOS ====================

    override suspend fun getSucursalesActivas(): Result<List<SucursalActivaDto>> {
        return try {
            val response = ApiClient.clienteService.getSucursalesActivas()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception(response.message() ?: "Error al obtener sucursales"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSucursal(sucursalId: Int): Result<SucursalActivaDto> {
        return try {
            val response = ApiClient.clienteService.getSucursal(sucursalId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.message() ?: "Error al obtener sucursal"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAreas(sucursalId: Int): Result<List<AreaClienteDto>> {
        return try {
            val response = ApiClient.clienteService.getAreas(sucursalId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception(response.message() ?: "Error al obtener áreas"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMesas(sucursalId: Int, areaId: Int?): Result<List<MesaClienteDto>> {
        return try {
            val response = ApiClient.clienteService.getMesas(sucursalId, areaId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception(response.message() ?: "Error al obtener mesas"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== MENÚ ====================

    override suspend fun getCategorias(): Result<List<CategoriaDto>> {
        return try {
            val response = ApiClient.clienteService.getCategorias()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception(response.message() ?: "Error al obtener categorías"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProductos(): Result<List<ProductoDto>> {
        return try {
            val response = ApiClient.clienteService.getProductos()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception(response.message() ?: "Error al obtener productos"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProducto(productoId: Int): Result<ProductoDto> {
        return try {
            val response = ApiClient.clienteService.getProducto(productoId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.message() ?: "Error al obtener producto"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCombos(): Result<List<ComboDto>> {
        return try {
            val response = ApiClient.clienteService.getCombos()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception(response.message() ?: "Error al obtener combos"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCombo(comboId: Int): Result<ComboDto> {
        return try {
            val response = ApiClient.clienteService.getCombo(comboId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.message() ?: "Error al obtener combo"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== HOLDS ====================

    override suspend fun verificarDisponibilidad(
        mesaId: Int,
        inicio: String,
        finEstimado: String
    ): Result<VerificarDisponibilidadResponse> {
        return try {
            val request = VerificarDisponibilidadRequest(mesaId, inicio, finEstimado)
            val response = ApiClient.clienteService.verificarDisponibilidad(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.message() ?: "Error al verificar disponibilidad"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun confirmarHold(holdId: Int): Result<ConfirmarHoldResponse> {
        return try {
            val response = ApiClient.clienteService.confirmarHold(holdId)
            if (response.isSuccessful && response.body() != null) {  // <-- Cambiar a solo verificar body
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.message() ?: "Error al confirmar hold"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun crearHold(
        mesaId: Int,
        inicio: String,
        horas: Int,
        ttlMinutes: Int,
        notas: String?
    ): Result<HoldDto> {
        return try {
            val request = CrearHoldRequest(
                mesa_id = mesaId,
                actor_tipo = 1,  // Cliente
                inicio = inicio,
                horas = horas,
                ttl_minutes = ttlMinutes,
                notas = notas
            )
            val response = ApiClient.clienteService.crearHold(request)
            if (response.isSuccessful && response.body()?.hold != null) {
                Result.success(response.body()!!.hold!!)  // <-- Agregar !! aquí
            } else {
                Result.failure(Exception(response.message() ?: "Error al crear hold"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun cancelarHold(holdId: Int, motivo: String?): Result<HoldDto> {
        return try {
            val request = if (motivo != null) CancelarHoldRequest(motivo) else null
            val response = ApiClient.clienteService.cancelarHold(holdId, request)
            if (response.isSuccessful && response.body()?.hold != null) {
                Result.success(response.body()!!.hold!!)  // <-- Agregar !! aquí
            } else {
                Result.failure(Exception(response.message() ?: "Error al cancelar hold"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== RESERVAS ====================

    override suspend fun crearReserva(
        holdId: Int,
        clienteId: Int,  // <-- Agregar parámetro
        inicio: String,
        finEstimado: String,
        toleranciaMin: Int,
        notas: String?
    ): Result<ReservaClienteDto> {
        return try {
            val request = CrearReservaClienteRequest(
                hold_id = holdId,
                cliente_id = clienteId,  // <-- Agregar esto
                inicio = inicio,
                fin_estimado = finEstimado,
                tolerancia_min = toleranciaMin,
                notas = notas
            )
            val response = ApiClient.clienteService.crearReserva(request)
            if (response.isSuccessful && response.body()?.reserva != null) {
                Result.success(response.body()!!.reserva!!)  // <-- Cambiar .data por .reserva
            } else {
                Result.failure(Exception(response.message() ?: "Error al crear reserva"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getReserva(reservaId: Int): Result<ReservaClienteDto> {
        return try {
            val response = ApiClient.clienteService.getReserva(reservaId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.message() ?: "Error al obtener reserva"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun listarReservas(
        clienteId: Int?,
        sucursalId: Int?,
        estatus: Int?,
        fechaDesde: String?,
        fechaHasta: String?
    ): Result<List<ReservaClienteDto>> {
        return try {
            val request = ListarReservasClienteRequest(
                cliente_id = clienteId,
                sucursal_id = sucursalId,
                estatus = estatus,
                fecha_desde = fechaDesde,
                fecha_hasta = fechaHasta
            )
            val response = ApiClient.clienteService.listarReservas(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.reservas ?: emptyList())
            } else {
                Result.failure(Exception(response.message() ?: "Error al listar reservas"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun cancelarReserva(reservaId: Int, motivo: String?): Result<ReservaClienteDto> {
        return try {
            val request = if (motivo != null) CancelarReservaRequest(motivo) else null
            val response = ApiClient.clienteService.cancelarReserva(reservaId, request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception(response.message() ?: "Error al cancelar reserva"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun iniciarReserva(reservaId: Int): Result<ReservaClienteDto> {
        return try {
            val response = ApiClient.clienteService.iniciarReserva(reservaId)
            if (response.isSuccessful && response.body()?.reserva != null) {
                Result.success(response.body()!!.reserva!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMsg = try {
                    org.json.JSONObject(errorBody ?: "").optString("error", "Error al iniciar reserva")
                } catch (e: Exception) {
                    response.message() ?: "Error al iniciar reserva"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun completarReserva(reservaId: Int): Result<ReservaClienteDto> {
        return try {
            val response = ApiClient.clienteService.completarReserva(reservaId)
            if (response.isSuccessful && response.body()?.reserva != null) {
                Result.success(response.body()!!.reserva!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMsg = try {
                    org.json.JSONObject(errorBody ?: "").optString("error", "Error al completar reserva")
                } catch (e: Exception) {
                    response.message() ?: "Error al completar reserva"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== PEDIDOS ====================

    override suspend fun crearPedidoDineIn(
        sucursalId: Int,
        clienteId: Int,
        reservaId: Int,
        notas: String?,
        items: List<ItemPedidoRequest>?
    ): Result<PedidoClienteDto> {
        return try {
            val request = CrearPedidoDineInRequest(
                sucursal_id = sucursalId,
                cliente_id = clienteId,
                tipo_pedido = 1,  // ← AGREGAR ESTO
                canal = 2,
                reserva_id = reservaId,
                notas = notas,
                items = items
            )
            val response = ApiClient.clienteService.crearPedidoDineIn(request)
            if (response.isSuccessful && response.body()?.pedido != null) {
                Result.success(response.body()!!.pedido)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMsg = try {
                    org.json.JSONObject(errorBody ?: "").optString("error", "Error al crear pedido")
                } catch (e: Exception) {
                    response.message() ?: "Error al crear pedido"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun crearPedidoTakeaway(
        sucursalId: Int,
        clienteId: Int,
        notas: String?,
        items: List<ItemPedidoRequest>?
    ): Result<PedidoClienteDto> {
        return try {
            val request = CrearPedidoTakeawayRequest(
                sucursal_id = sucursalId,
                cliente_id = clienteId,
                notas = notas,
                items = items
            )
            val response = ApiClient.clienteService.crearPedidoTakeaway(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.pedido)
            } else {
                Result.failure(Exception(response.message() ?: "Error al crear pedido takeaway"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun agregarItem(
        pedidoId: Int,
        productoId: Int?,
        comboId: Int?,
        cantidad: Int,
        notas: String?
    ): Result<ItemPedidoDto> {
        return try {
            val request = ItemPedidoRequest(
                producto_id = productoId,
                combo_id = comboId,
                cantidad = cantidad,
                notas = notas
            )
            val response = ApiClient.clienteService.agregarItem(pedidoId, request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.item)
            } else {
                Result.failure(Exception(response.message() ?: "Error al agregar item"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPedido(pedidoId: Int): Result<PedidoClienteDto> {
        return try {
            val response = ApiClient.clienteService.getPedido(pedidoId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.message() ?: "Error al obtener pedido"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun listarPedidos(
        clienteId: Int?,
        sucursalId: Int?,
        estado: Int?,
        tipoPedido: Int?,
        fechaDesde: String?,
        fechaHasta: String?
    ): Result<List<PedidoClienteDto>> {
        return try {
            val request = ListarPedidosRequest(
                cliente_id = clienteId,
                sucursal_id = sucursalId,
                estado = estado,
                tipo_pedido = tipoPedido,
                fecha_desde = fechaDesde,
                fecha_hasta = fechaHasta
            )
            val response = ApiClient.clienteService.listarPedidos(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.pedidos ?: emptyList())
            } else {
                Result.failure(Exception(response.message() ?: "Error al listar pedidos"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPedidosActivos(sucursalId: Int): Result<List<PedidoClienteDto>> {
        return try {
            val response = ApiClient.clienteService.getPedidosActivos(sucursalId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.pedidos ?: emptyList())
            } else {
                Result.failure(Exception(response.message() ?: "Error al obtener pedidos activos"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun completarPedido(pedidoId: Int): Result<PedidoClienteDto> {
        return try {
            val response = ApiClient.clienteService.completarPedido(pedidoId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.pedido)
            } else {
                Result.failure(Exception(response.message() ?: "Error al completar pedido"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun cancelarPedido(pedidoId: Int, comentario: String?): Result<PedidoClienteDto> {
        return try {
            val request = if (comentario != null) AccionPedidoRequest(comentario) else null
            val response = ApiClient.clienteService.cancelarPedido(pedidoId, request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.pedido)
            } else {
                Result.failure(Exception(response.message() ?: "Error al cancelar pedido"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== CUPONES ====================

    override suspend fun getMisCupones(vigentes: Boolean): Result<List<CuponClienteDto>> {
        return try {
            val response = ApiClient.clienteService.getMisCupones(vigentes)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.cupones)
            } else {
                Result.failure(Exception(response.message() ?: "Error al obtener cupones"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCuponesCliente(clienteId: Int): Result<List<CuponClienteDto>> {
        return try {
            val response = ApiClient.clienteService.getCuponesCliente(clienteId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.cupones)
            } else {
                Result.failure(Exception(response.message() ?: "Error al obtener cupones del cliente"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun validarCupon(codigo: String, clienteId: Int): Result<ValidarCuponResponse> {
        return try {
            val request = ValidarCuponRequest(codigo, clienteId)
            val response = ApiClient.clienteService.validarCupon(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.message() ?: "Error al validar cupón"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCampanias(): Result<List<CampaniaDto>> {
        return try {
            val response = ApiClient.clienteService.getCampanias()
            if (response.isSuccessful && response.body()?.campanias != null) {
                Result.success(response.body()!!.campanias!!)
            } else {
                Result.failure(Exception(response.message() ?: "Error al obtener campañas"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== PAGOS ====================

    override suspend fun crearPago(
        pedidoId: Int,
        sucursalId: Int,
        monto: Double,
        propina: Double,
        campaniaUsuarioId: Int?,
        montoDescontado: Double?
    ): Result<PagoDto> {
        return try {
            val request = CrearPagoRequest(
                pedido_id = pedidoId,
                sucursal_id = sucursalId,
                monto = monto,
                propina = propina,
                campania_usuario_id = campaniaUsuarioId,
                monto_descontado = montoDescontado
            )
            val response = ApiClient.clienteService.crearPago(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception(response.message() ?: "Error al crear pago"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== CALIFICACIONES ====================

    override suspend fun crearCalificacion(
        pedidoId: Int,
        calificacion: Int,
        empleadoId: Int?,
        notas: String?
    ): Result<CalificacionDto> {
        return try {
            val request = CrearCalificacionRequest(
                pedido_id = pedidoId,
                calificacion = calificacion,
                empleado_id = empleadoId,
                notas = notas
            )
            val response = ApiClient.clienteService.crearCalificacion(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception(response.message() ?: "Error al crear calificación"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCalificaciones(
        pedidoId: Int?,
        empleadoId: Int?
    ): Result<List<CalificacionDto>> {
        return try {
            val response = ApiClient.clienteService.getCalificaciones(pedidoId, empleadoId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception(response.message() ?: "Error al obtener calificaciones"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCalificacion(calificacionId: Int): Result<CalificacionDto> {
        return try {
            val response = ApiClient.clienteService.getCalificacion(calificacionId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.message() ?: "Error al obtener calificación"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== MEJORAS ====================

    override suspend fun crearMejora(notas: String): Result<MejoraDto> {
        return try {
            val request = CrearMejoraRequest(notas)
            val response = ApiClient.clienteService.crearMejora(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception(response.message() ?: "Error al crear mejora"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMejoras(estatus: Int?): Result<List<MejoraDto>> {
        return try {
            val response = ApiClient.clienteService.getMejoras(estatus)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception(response.message() ?: "Error al obtener mejoras"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMejora(mejoraId: Int): Result<MejoraDto> {
        return try {
            val response = ApiClient.clienteService.getMejora(mejoraId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.message() ?: "Error al obtener mejora"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
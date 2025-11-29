package com.intellisoft.androidcuisine.data.repository

import com.intellisoft.androidcuisine.data.remote.api.ApiClient
import com.intellisoft.androidcuisine.data.remote.dto.*
import com.intellisoft.androidcuisine.domain.repository.ClienteRepository

class ClienteRepositoryImpl : ClienteRepository {

    // ==================== SUCURSALES ====================

    override suspend fun getSucursalesActivas(): Result<List<SucursalActivaDto>> {
        return try {
            val response = ApiClient.clienteService.getSucursalesActivas()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error al obtener sucursales"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== ÁREAS ====================

    override suspend fun getAreas(sucursalId: Int): Result<List<AreaDto>> {
        return try {
            val response = ApiClient.clienteService.getAreas(sucursalId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error al obtener áreas"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== MESAS ====================

    override suspend fun getMesas(sucursalId: Int, areaId: Int?): Result<List<MesaClienteDto>> {
        return try {
            val response = ApiClient.clienteService.getMesas(sucursalId, areaId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error al obtener mesas"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== HOLDS ====================

    override suspend fun crearHold(mesaId: Int, inicio: String, fin: String): Result<HoldClienteDto> {
        return try {
            val request = CrearHoldClienteRequest(
                mesa_id = mesaId,
                inicio = inicio,
                fin = fin
            )
            val response = ApiClient.clienteService.crearHold(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.hold)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error al crear hold"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun cancelarHold(holdId: Int): Result<Unit> {
        return try {
            ApiClient.clienteService.cancelarHold(holdId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== RESERVAS ====================

    override suspend fun crearReserva(
        clienteId: Int,
        holdId: Int,
        inicio: String,
        finEstimado: String,
        toleranciaMin: Int,
        notas: String?
    ): Result<ReservaClienteDto> {
        return try {
            val request = CrearReservaClienteRequest(
                cliente_id = clienteId,
                hold_id = holdId,
                inicio = inicio,
                fin_estimado = finEstimado,
                tolerancia_min = toleranciaMin,
                notas = notas
            )
            val response = ApiClient.clienteService.crearReserva(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.reserva)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error al crear reserva"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun listarReservas(clienteId: Int, estado: Int?): Result<List<ReservaClienteDto>> {
        return try {
            val request = ListarReservasClienteRequest(
                cliente_id = clienteId,
                estado = estado
            )
            val response = ApiClient.clienteService.listarReservas(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.reservas)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error al listar reservas"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun cancelarReserva(reservaId: Int, motivo: String?): Result<String> {
        return try {
            val request = if (motivo != null) CancelarReservaClienteRequest(motivo) else null
            val response = ApiClient.clienteService.cancelarReserva(reservaId, request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.mensaje)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error al cancelar reserva"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== PEDIDOS ====================

    override suspend fun crearPedidoDineIn(request: CrearPedidoDineInRequest): Result<PedidoCreadoDto> {
        return try {
            val response = ApiClient.clienteService.crearPedidoDineIn(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.pedido)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error al crear pedido"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun crearPedidoTakeaway(request: CrearPedidoTakeawayRequest): Result<PedidoCreadoDto> {
        return try {
            val response = ApiClient.clienteService.crearPedidoTakeaway(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.pedido)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error al crear pedido para llevar"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPedido(pedidoId: Int): Result<PedidoDetalleDto> {
        return try {
            val response = ApiClient.clienteService.getPedido(pedidoId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.pedido)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error al obtener pedido"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun agregarItem(pedidoId: Int, request: AgregarItemRequest): Result<ItemAgregadoDto> {
        return try {
            val response = ApiClient.clienteService.agregarItem(pedidoId, request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.item)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error al agregar item"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun completarPedido(pedidoId: Int): Result<String> {
        return try {
            val response = ApiClient.clienteService.completarPedido(pedidoId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.mensaje)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error al completar pedido"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun cancelarPedido(pedidoId: Int, comentario: String?): Result<String> {
        return try {
            val request = if (comentario != null) AccionPedidoRequest(comentario) else null
            val response = ApiClient.clienteService.cancelarPedido(pedidoId, request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.mensaje)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error al cancelar pedido"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun listarPedidos(
        clienteId: Int,
        sucursalId: Int?,
        estado: Int?,
        tipoPedido: Int?
    ): Result<List<PedidoDetalleDto>> {
        return try {
            val request = ListarPedidosClienteRequest(
                cliente_id = clienteId,
                sucursal_id = sucursalId,
                estado = estado,
                tipo_pedido = tipoPedido
            )
            val response = ApiClient.clienteService.listarPedidos(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.pedidos)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error al listar pedidos"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== CUPONES ====================

    override suspend fun getMisCupones(soloDisponibles: Boolean): Result<List<CuponClienteDto>> {
        return try {
            val response = ApiClient.clienteService.getMisCupones(soloDisponibles)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.cupones)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error al obtener cupones"
                Result.failure(Exception(errorMsg))
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
                val errorMsg = response.errorBody()?.string() ?: "Error al validar cupón"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== CALIFICACIONES ====================

    override suspend fun crearCalificacion(request: CrearCalificacionRequest): Result<CalificacionDto> {
        return try {
            val response = ApiClient.clienteService.crearCalificacion(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.calificacion)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error al crear calificación"
                Result.failure(Exception(errorMsg))
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
                val errorMsg = response.errorBody()?.string() ?: "Error al obtener categorías"
                Result.failure(Exception(errorMsg))
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
                val errorMsg = response.errorBody()?.string() ?: "Error al obtener productos"
                Result.failure(Exception(errorMsg))
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
                val errorMsg = response.errorBody()?.string() ?: "Error al obtener combos"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
package com.intellisoft.androidcuisine.views.reserva

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.intellisoft.androidcuisine.data.remote.dto.*
import com.intellisoft.androidcuisine.data.repository.ClienteRepositoryImpl
import com.intellisoft.androidcuisine.domain.repository.ClienteRepository
import kotlinx.coroutines.launch

class PedidosClienteViewModel(application: Application) : AndroidViewModel(application) {

    private val clienteRepository: ClienteRepository = ClienteRepositoryImpl()

    private var clienteId: Int = 0
    private var sucursalId: Int? = null

    // ==================== CARRITO ====================

    private val _carritoItems = MutableLiveData<MutableList<CarritoItem>>(mutableListOf())
    val carritoItems: LiveData<MutableList<CarritoItem>> = _carritoItems

    private val _totalCarrito = MutableLiveData<Double>(0.0)
    val totalCarrito: LiveData<Double> = _totalCarrito

    // ==================== MENÚ ====================

    private val _categoriasState = MutableLiveData<CategoriasState>()
    val categoriasState: LiveData<CategoriasState> = _categoriasState

    private val _productosState = MutableLiveData<ProductosState>()
    val productosState: LiveData<ProductosState> = _productosState

    private val _combosState = MutableLiveData<CombosState>()
    val combosState: LiveData<CombosState> = _combosState

    private val _categoriaSeleccionada = MutableLiveData<Int?>(null)
    val categoriaSeleccionada: LiveData<Int?> = _categoriaSeleccionada

    // ==================== PEDIDOS ====================

    private val _misPedidosState = MutableLiveData<MisPedidosState>()
    val misPedidosState: LiveData<MisPedidosState> = _misPedidosState

    private val _pedidoDetalleState = MutableLiveData<PedidoDetalleState>()
    val pedidoDetalleState: LiveData<PedidoDetalleState> = _pedidoDetalleState

    private val _crearPedidoState = MutableLiveData<CrearPedidoClienteState>()
    val crearPedidoState: LiveData<CrearPedidoClienteState> = _crearPedidoState

    private val _accionPedidoState = MutableLiveData<AccionPedidoClienteState>()
    val accionPedidoState: LiveData<AccionPedidoClienteState> = _accionPedidoState

    // ==================== CONFIGURACIÓN ====================

    fun configurarCliente(clienteId: Int, sucursalId: Int?) {
        this.clienteId = clienteId
        this.sucursalId = sucursalId
    }

    fun getClienteId(): Int = clienteId
    fun getSucursalId(): Int? = sucursalId

    fun setSucursalId(id: Int) {
        this.sucursalId = id
    }

    // ==================== MENÚ ====================

    fun loadCategorias() {
        _categoriasState.value = CategoriasState.Loading

        viewModelScope.launch {
            val result = clienteRepository.getCategorias()

            result.fold(
                onSuccess = { categorias ->
                    val activas = categorias.filter { it.es_activa }
                    if (activas.isEmpty()) {
                        _categoriasState.value = CategoriasState.Empty
                    } else {
                        _categoriasState.value = CategoriasState.Success(activas)
                    }
                },
                onFailure = { exception ->
                    _categoriasState.value = CategoriasState.Error(exception.message ?: "Error desconocido")
                }
            )
        }
    }

    fun loadProductos() {
        _productosState.value = ProductosState.Loading

        viewModelScope.launch {
            val result = clienteRepository.getProductos()

            result.fold(
                onSuccess = { productos ->
                    val activos = productos.filter { it.es_activo }
                    if (activos.isEmpty()) {
                        _productosState.value = ProductosState.Empty
                    } else {
                        _productosState.value = ProductosState.Success(activos)
                    }
                },
                onFailure = { exception ->
                    _productosState.value = ProductosState.Error(exception.message ?: "Error desconocido")
                }
            )
        }
    }

    fun loadCombos() {
        _combosState.value = CombosState.Loading

        viewModelScope.launch {
            val result = clienteRepository.getCombos()

            result.fold(
                onSuccess = { combos ->
                    val activos = combos.filter { it.es_activo }
                    if (activos.isEmpty()) {
                        _combosState.value = CombosState.Empty
                    } else {
                        _combosState.value = CombosState.Success(activos)
                    }
                },
                onFailure = { exception ->
                    _combosState.value = CombosState.Error(exception.message ?: "Error desconocido")
                }
            )
        }
    }

    fun seleccionarCategoria(categoriaId: Int?) {
        _categoriaSeleccionada.value = categoriaId
    }

    fun getProductosFiltrados(): List<ProductoDto> {
        val state = _productosState.value
        if (state !is ProductosState.Success) return emptyList()

        val categoriaId = _categoriaSeleccionada.value
        return if (categoriaId == null) {
            state.productos
        } else {
            state.productos.filter { it.categoria_id == categoriaId }
        }
    }

    // ==================== CARRITO ====================

    fun agregarAlCarrito(producto: ProductoDto, cantidad: Int = 1, notas: String? = null) {
        val lista = _carritoItems.value ?: mutableListOf()

        val existente = lista.find { it.productoId == producto.id_producto && it.comboId == null }
        if (existente != null) {
            existente.cantidad += cantidad
        } else {
            lista.add(
                CarritoItem(
                    productoId = producto.id_producto,
                    comboId = null,
                    nombre = producto.nombre,
                    precio = producto.precio,
                    cantidad = cantidad,
                    notas = notas
                )
            )
        }

        _carritoItems.value = lista
        calcularTotal()
    }

    fun agregarComboAlCarrito(combo: ComboDto, cantidad: Int = 1, notas: String? = null) {
        val lista = _carritoItems.value ?: mutableListOf()

        val existente = lista.find { it.comboId == combo.id_combo && it.productoId == null }
        if (existente != null) {
            existente.cantidad += cantidad
        } else {
            lista.add(
                CarritoItem(
                    productoId = null,
                    comboId = combo.id_combo,
                    nombre = combo.nombre,
                    precio = combo.precio,
                    cantidad = cantidad,
                    notas = notas
                )
            )
        }

        _carritoItems.value = lista
        calcularTotal()
    }

    fun actualizarCantidad(item: CarritoItem, nuevaCantidad: Int) {
        val lista = _carritoItems.value ?: mutableListOf()

        if (nuevaCantidad <= 0) {
            lista.remove(item)
        } else {
            item.cantidad = nuevaCantidad
        }

        _carritoItems.value = lista
        calcularTotal()
    }

    fun eliminarDelCarrito(item: CarritoItem) {
        val lista = _carritoItems.value ?: mutableListOf()
        lista.remove(item)
        _carritoItems.value = lista
        calcularTotal()
    }

    fun limpiarCarrito() {
        _carritoItems.value = mutableListOf()
        _totalCarrito.value = 0.0
    }

    private fun calcularTotal() {
        val lista = _carritoItems.value ?: mutableListOf()
        val total = lista.sumOf { it.precio * it.cantidad }
        _totalCarrito.value = total
    }

    fun getCantidadEnCarrito(): Int {
        return _carritoItems.value?.sumOf { it.cantidad } ?: 0
    }

    // ==================== PEDIDOS ====================

    fun loadMisPedidos(estado: Int? = null, tipoPedido: Int? = null) {
        _misPedidosState.value = MisPedidosState.Loading

        viewModelScope.launch {
            val result = clienteRepository.listarPedidos(clienteId, sucursalId, estado, tipoPedido)

            result.fold(
                onSuccess = { pedidos ->
                    if (pedidos.isEmpty()) {
                        _misPedidosState.value = MisPedidosState.Empty
                    } else {
                        _misPedidosState.value = MisPedidosState.Success(pedidos)
                    }
                },
                onFailure = { exception ->
                    _misPedidosState.value = MisPedidosState.Error(exception.message ?: "Error desconocido")
                }
            )
        }
    }

    fun getPedidoDetalle(pedidoId: Int) {
        _pedidoDetalleState.value = PedidoDetalleState.Loading

        viewModelScope.launch {
            val result = clienteRepository.getPedido(pedidoId)

            result.fold(
                onSuccess = { pedido ->
                    _pedidoDetalleState.value = PedidoDetalleState.Success(pedido)
                },
                onFailure = { exception ->
                    _pedidoDetalleState.value = PedidoDetalleState.Error(exception.message ?: "Error desconocido")
                }
            )
        }
    }

    fun crearPedidoTakeaway(notas: String? = null) {
        val sucursal = sucursalId ?: run {
            _crearPedidoState.value = CrearPedidoClienteState.Error("Selecciona una sucursal primero")
            return
        }

        val items = _carritoItems.value
        if (items.isNullOrEmpty()) {
            _crearPedidoState.value = CrearPedidoClienteState.Error("El carrito está vacío")
            return
        }

        _crearPedidoState.value = CrearPedidoClienteState.Loading

        viewModelScope.launch {
            val itemsRequest = items.map { item ->
                ItemPedidoRequest(
                    producto_id = item.productoId,
                    combo_id = item.comboId,
                    cantidad = item.cantidad,
                    notas = item.notas
                )
            }

            val request = CrearPedidoTakeawayRequest(
                sucursal_id = sucursal,
                cliente_id = clienteId,
                items = itemsRequest,
                canal = "App",
                notas = notas
            )

            val result = clienteRepository.crearPedidoTakeaway(request)

            result.fold(
                onSuccess = { pedido ->
                    limpiarCarrito()
                    _crearPedidoState.value = CrearPedidoClienteState.Success(pedido)
                },
                onFailure = { exception ->
                    _crearPedidoState.value = CrearPedidoClienteState.Error(exception.message ?: "Error desconocido")
                }
            )
        }
    }

    fun crearPedidoDineIn(reservaId: Int, notas: String? = null) {
        val sucursal = sucursalId ?: run {
            _crearPedidoState.value = CrearPedidoClienteState.Error("Selecciona una sucursal primero")
            return
        }

        val items = _carritoItems.value
        if (items.isNullOrEmpty()) {
            _crearPedidoState.value = CrearPedidoClienteState.Error("El carrito está vacío")
            return
        }

        _crearPedidoState.value = CrearPedidoClienteState.Loading

        viewModelScope.launch {
            val itemsRequest = items.map { item ->
                ItemPedidoRequest(
                    producto_id = item.productoId,
                    combo_id = item.comboId,
                    cantidad = item.cantidad,
                    notas = item.notas
                )
            }

            val request = CrearPedidoDineInRequest(
                sucursal_id = sucursal,
                cliente_id = clienteId,
                reserva_id = reservaId,
                items = itemsRequest,
                canal = "App",
                notas = notas
            )

            val result = clienteRepository.crearPedidoDineIn(request)

            result.fold(
                onSuccess = { pedido ->
                    limpiarCarrito()
                    _crearPedidoState.value = CrearPedidoClienteState.Success(pedido)
                },
                onFailure = { exception ->
                    _crearPedidoState.value = CrearPedidoClienteState.Error(exception.message ?: "Error desconocido")
                }
            )
        }
    }

    fun cancelarPedido(pedidoId: Int, comentario: String? = null) {
        _accionPedidoState.value = AccionPedidoClienteState.Loading

        viewModelScope.launch {
            val result = clienteRepository.cancelarPedido(pedidoId, comentario)

            result.fold(
                onSuccess = { mensaje ->
                    _accionPedidoState.value = AccionPedidoClienteState.Success(mensaje)
                },
                onFailure = { exception ->
                    _accionPedidoState.value = AccionPedidoClienteState.Error(exception.message ?: "Error desconocido")
                }
            )
        }
    }

    // ==================== RESETS ====================

    fun resetCrearPedidoState() {
        _crearPedidoState.value = CrearPedidoClienteState.Idle
    }

    fun resetAccionPedidoState() {
        _accionPedidoState.value = AccionPedidoClienteState.Idle
    }

    fun resetPedidoDetalleState() {
        _pedidoDetalleState.value = PedidoDetalleState.Idle
    }
}

// ==================== CARRITO ITEM ====================

data class CarritoItem(
    val productoId: Int?,
    val comboId: Int?,
    val nombre: String,
    val precio: Double,
    var cantidad: Int,
    var notas: String?
)

// ==================== STATES ====================

sealed class CategoriasState {
    object Loading : CategoriasState()
    object Empty : CategoriasState()
    data class Success(val categorias: List<CategoriaDto>) : CategoriasState()
    data class Error(val message: String) : CategoriasState()
}

sealed class ProductosState {
    object Loading : ProductosState()
    object Empty : ProductosState()
    data class Success(val productos: List<ProductoDto>) : ProductosState()
    data class Error(val message: String) : ProductosState()
}

sealed class CombosState {
    object Loading : CombosState()
    object Empty : CombosState()
    data class Success(val combos: List<ComboDto>) : CombosState()
    data class Error(val message: String) : CombosState()
}

sealed class MisPedidosState {
    object Loading : MisPedidosState()
    object Empty : MisPedidosState()
    data class Success(val pedidos: List<PedidoDetalleDto>) : MisPedidosState()
    data class Error(val message: String) : MisPedidosState()
}

sealed class PedidoDetalleState {
    object Idle : PedidoDetalleState()
    object Loading : PedidoDetalleState()
    data class Success(val pedido: PedidoDetalleDto) : PedidoDetalleState()
    data class Error(val message: String) : PedidoDetalleState()
}

sealed class CrearPedidoClienteState {
    object Idle : CrearPedidoClienteState()
    object Loading : CrearPedidoClienteState()
    data class Success(val pedido: PedidoCreadoDto) : CrearPedidoClienteState()
    data class Error(val message: String) : CrearPedidoClienteState()
}

sealed class AccionPedidoClienteState {
    object Idle : AccionPedidoClienteState()
    object Loading : AccionPedidoClienteState()
    data class Success(val mensaje: String) : AccionPedidoClienteState()
    data class Error(val message: String) : AccionPedidoClienteState()
}
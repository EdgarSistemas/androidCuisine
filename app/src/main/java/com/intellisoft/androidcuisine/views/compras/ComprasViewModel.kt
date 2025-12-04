package com.intellisoft.androidcuisine.views.compras

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.intellisoft.androidcuisine.data.remote.dto.CompraData
import com.intellisoft.androidcuisine.data.remote.dto.CompraDetalleCompleta
import com.intellisoft.androidcuisine.data.remote.dto.CompraRequest
import com.intellisoft.androidcuisine.data.remote.dto.InsumoExistenciaDto
import com.intellisoft.androidcuisine.data.remote.dto.ProveedorDto
import com.intellisoft.androidcuisine.domain.repository.compras.ComprasRepositoryImpl
import com.intellisoft.androidcuisine.domain.repository.insumo.InsumoRepositoryImpl
import com.intellisoft.androidcuisine.domain.repository.proveedor.ProveedorRepositoryImpl
import com.intellisoft.androidcuisine.domain.repository.compras.ComprasRepository
import com.intellisoft.androidcuisine.domain.repository.insumo.InsumoRepository
import com.intellisoft.androidcuisine.domain.repository.proveedor.ProveedorRepository
import com.intellisoft.androidcuisine.util.SessionManager
import kotlinx.coroutines.launch

class ComprasViewModel(application: Application) : AndroidViewModel(application) {

    private val proveedorRepository: ProveedorRepository = ProveedorRepositoryImpl()
    private val insumoRepository: InsumoRepository = InsumoRepositoryImpl()

    private val _proveedoresState = MutableLiveData<ProveedoresState>()
    val proveedoresState: LiveData<ProveedoresState> = _proveedoresState

    private val _insumosState = MutableLiveData<InsumosState>()
    val insumosState: LiveData<InsumosState> = _insumosState

    private val comprasRepository: ComprasRepository = ComprasRepositoryImpl()
    private val sessionManager = SessionManager.getInstance(application)

    private val _comprasState = MutableLiveData<ComprasState>()
    val comprasState: LiveData<ComprasState> = _comprasState

    private val _detalleCompraState = MutableLiveData<DetalleCompraState>()
    val detalleCompraState: LiveData<DetalleCompraState> = _detalleCompraState

    private val _crearCompraState = MutableLiveData<CrearCompraState>()
    val crearCompraState: LiveData<CrearCompraState> = _crearCompraState

    fun loadCompras(sucursalId: Int) {
        _comprasState.value = ComprasState.Loading

        viewModelScope.launch {
            val result = comprasRepository.listarCompras(sucursalId)

            result.fold(
                onSuccess = { compras ->
                    if (compras.isEmpty()) {
                        _comprasState.value = ComprasState.Empty
                    } else {
                        _comprasState.value = ComprasState.Success(compras)
                    }
                },
                onFailure = { exception ->
                    _comprasState.value = ComprasState.Error(exception.message ?: "Error desconocido")
                }
            )
        }
    }

    fun loadDetalleCompra(compraId: Int) {
        _detalleCompraState.value = DetalleCompraState.Loading

        viewModelScope.launch {
            val result = comprasRepository.obtenerCompra(compraId)

            result.fold(
                onSuccess = { detalle ->
                    _detalleCompraState.value = DetalleCompraState.Success(detalle)
                },
                onFailure = { exception ->
                    _detalleCompraState.value = DetalleCompraState.Error(exception.message ?: "Error desconocido")
                }
            )
        }
    }

    fun crearCompra(compra: CompraRequest) {
        _crearCompraState.value = CrearCompraState.Loading

        viewModelScope.launch {
            val result = comprasRepository.crearCompra(compra)

            result.fold(
                onSuccess = { compraData ->
                    _crearCompraState.value = CrearCompraState.Success(compraData.folio)
                },
                onFailure = { exception ->
                    _crearCompraState.value = CrearCompraState.Error(exception.message ?: "Error desconocido")
                }
            )
        }
    }

    fun cancelarCompra(compraId: Int) {
        viewModelScope.launch {
            val result = comprasRepository.cancelarCompra(compraId)

            result.fold(
                onSuccess = { message ->
                    // Recargar la compra después de cancelar
                    loadDetalleCompra(compraId)
                },
                onFailure = { exception ->
                    _detalleCompraState.value = DetalleCompraState.Error(exception.message ?: "Error al cancelar")
                }
            )
        }
    }

    fun loadProveedores() {
        _proveedoresState.value = ProveedoresState.Loading

        viewModelScope.launch {
            val result = proveedorRepository.getProveedores()

            result.fold(
                onSuccess = { proveedores ->
                    _proveedoresState.value = ProveedoresState.Success(proveedores)
                },
                onFailure = { exception ->
                    _proveedoresState.value = ProveedoresState.Error(exception.message ?: "Error desconocido")
                }
            )
        }
    }

    fun loadInsumos(sucursalId: Int) {
        _insumosState.value = InsumosState.Loading

        viewModelScope.launch {
            val result = insumoRepository.getInsumosExistencias(sucursalId)

            result.fold(
                onSuccess = { insumos ->
                    _insumosState.value = InsumosState.Success(insumos)
                },
                onFailure = { exception ->
                    _insumosState.value = InsumosState.Error(exception.message ?: "Error desconocido")
                }
            )
        }
    }

}

sealed class ComprasState {
    object Loading : ComprasState()
    object Empty : ComprasState()
    data class Success(val compras: List<CompraData>) : ComprasState()
    data class Error(val message: String) : ComprasState()
}

sealed class DetalleCompraState {
    object Loading : DetalleCompraState()
    data class Success(val detalle: CompraDetalleCompleta) : DetalleCompraState()
    data class Error(val message: String) : DetalleCompraState()
}

sealed class CrearCompraState {
    object Idle : CrearCompraState()
    object Loading : CrearCompraState()
    data class Success(val folio: String) : CrearCompraState()
    data class Error(val message: String) : CrearCompraState()
}

sealed class ProveedoresState {
    object Loading : ProveedoresState()
    data class Success(val proveedores: List<ProveedorDto>) : ProveedoresState()
    data class Error(val message: String) : ProveedoresState()
}

sealed class InsumosState {
    object Loading : InsumosState()
    data class Success(val insumos: List<InsumoExistenciaDto>) : InsumosState()
    data class Error(val message: String) : InsumosState()
}
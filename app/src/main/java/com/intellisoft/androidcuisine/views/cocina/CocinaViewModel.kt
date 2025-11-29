package com.intellisoft.androidcuisine.views.cocina

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.intellisoft.androidcuisine.data.remote.dto.*
import com.intellisoft.androidcuisine.data.repository.CocinaRepositoryImpl
import com.intellisoft.androidcuisine.domain.repository.CocinaRepository
import kotlinx.coroutines.launch

class CocinaViewModel(application: Application) : AndroidViewModel(application) {

    private val cocinaRepository: CocinaRepository = CocinaRepositoryImpl()

    private val _itemsPendientesState = MutableLiveData<ItemsPendientesState>()
    val itemsPendientesState: LiveData<ItemsPendientesState> = _itemsPendientesState

    private val _pedidosCocinaState = MutableLiveData<PedidosCocinaState>()
    val pedidosCocinaState: LiveData<PedidosCocinaState> = _pedidosCocinaState

    private val _marcarListoState = MutableLiveData<MarcarListoState>()
    val marcarListoState: LiveData<MarcarListoState> = _marcarListoState

    fun loadItemsPendientes(sucursalId: Int? = null) {
        _itemsPendientesState.value = ItemsPendientesState.Loading

        viewModelScope.launch {
            val result = cocinaRepository.getItemsPendientes(sucursalId)

            result.fold(
                onSuccess = { items ->
                    if (items.isEmpty()) {
                        _itemsPendientesState.value = ItemsPendientesState.Empty
                    } else {
                        _itemsPendientesState.value = ItemsPendientesState.Success(items)
                    }
                },
                onFailure = { exception ->
                    _itemsPendientesState.value = ItemsPendientesState.Error(exception.message ?: "Error desconocido")
                }
            )
        }
    }

    fun loadPedidosEnCocina(sucursalId: Int? = null) {
        _pedidosCocinaState.value = PedidosCocinaState.Loading

        viewModelScope.launch {
            val result = cocinaRepository.getPedidosEnCocina(sucursalId)

            result.fold(
                onSuccess = { pedidos ->
                    if (pedidos.isEmpty()) {
                        _pedidosCocinaState.value = PedidosCocinaState.Empty
                    } else {
                        _pedidosCocinaState.value = PedidosCocinaState.Success(pedidos)
                    }
                },
                onFailure = { exception ->
                    _pedidosCocinaState.value = PedidosCocinaState.Error(exception.message ?: "Error desconocido")
                }
            )
        }
    }

    fun marcarItemListo(itemId: Int) {
        _marcarListoState.value = MarcarListoState.Loading

        viewModelScope.launch {
            val result = cocinaRepository.marcarItemListo(itemId)

            result.fold(
                onSuccess = { response ->
                    _marcarListoState.value = MarcarListoState.Success(response)
                },
                onFailure = { exception ->
                    _marcarListoState.value = MarcarListoState.Error(exception.message ?: "Error desconocido")
                }
            )
        }
    }

    fun resetMarcarListoState() {
        _marcarListoState.value = MarcarListoState.Idle
    }
}

// States
sealed class ItemsPendientesState {
    object Loading : ItemsPendientesState()
    object Empty : ItemsPendientesState()
    data class Success(val items: List<ItemPendienteDto>) : ItemsPendientesState()
    data class Error(val message: String) : ItemsPendientesState()
}

sealed class PedidosCocinaState {
    object Loading : PedidosCocinaState()
    object Empty : PedidosCocinaState()
    data class Success(val pedidos: List<PedidoCocinaDto>) : PedidosCocinaState()
    data class Error(val message: String) : PedidosCocinaState()
}

sealed class MarcarListoState {
    object Idle : MarcarListoState()
    object Loading : MarcarListoState()
    data class Success(val response: MarcarListoResponse) : MarcarListoState()
    data class Error(val message: String) : MarcarListoState()
}
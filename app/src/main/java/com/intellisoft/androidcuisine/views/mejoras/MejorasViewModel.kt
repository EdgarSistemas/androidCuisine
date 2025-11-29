package com.intellisoft.androidcuisine.views.mejoras

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.intellisoft.androidcuisine.data.remote.dto.MejoraItemDto
import com.intellisoft.androidcuisine.domain.repository.mejoras.MejorasRepository
import com.intellisoft.androidcuisine.domain.repository.mejoras.MejorasRepositoryImpl
import kotlinx.coroutines.launch

class MejorasViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MejorasRepository = MejorasRepositoryImpl()

    // Estado para el envío de mejoras (POST)
    private val _state = MutableLiveData<MejorasState>()
    val state: LiveData<MejorasState> = _state

    // Estado para la lista de mejoras (GET)
    private val _listState = MutableLiveData<MejorasListState>()
    val listState: LiveData<MejorasListState> = _listState

    // -------------------------
    // LÓGICA PARA ENVIAR (POST)
    // -------------------------
    fun enviarSugerencia(notas: String) {
        if (notas.isBlank()) {
            _state.value = MejorasState.Error("El campo no puede estar vacío.")
            return
        }
        if (notas.length > 300) {
            _state.value = MejorasState.Error("El texto excede los 300 caracteres permitidos.")
            return
        }

        _state.value = MejorasState.Loading // Sin paréntesis porque es object

        viewModelScope.launch {
            val result = repository.enviarMejora(notas)
            result.fold(
                onSuccess = { itemCreado ->
                    _state.value = MejorasState.Success("Sugerencia enviada ID: ${itemCreado.idMejora}")
                    // Al enviar con éxito, recargamos la lista automáticamente
                    cargarMejoras()
                },
                onFailure = { exception ->
                    _state.value = MejorasState.Error(exception.message ?: "Error de conexión")
                }
            )
        }
    }

    fun resetState() {
        _state.value = MejorasState.Idle // Sin paréntesis
    }

    // -------------------------
    // LÓGICA PARA LISTAR (GET)
    // -------------------------
    fun cargarMejoras(estatus: Int? = null) {
        _listState.value = MejorasListState.Loading // Sin paréntesis

        viewModelScope.launch {
            val result = repository.getMejoras(estatus)
            result.fold(
                onSuccess = { lista ->
                    if (lista.isEmpty()) {
                        _listState.value = MejorasListState.Empty // Sin paréntesis
                    } else {
                        _listState.value = MejorasListState.Success(lista) // Con paréntesis (data class)
                    }
                },
                onFailure = { e ->
                    _listState.value = MejorasListState.Error(e.message ?: "Error al cargar") // Con paréntesis
                }
            )
        }
    }
}

// Estados para el formulario de envío
sealed class MejorasState {
    object Idle : MejorasState()
    object Loading : MejorasState()
    data class Success(val message: String) : MejorasState()
    data class Error(val message: String) : MejorasState()
}

// Estados para la lista
sealed class MejorasListState {
    object Loading : MejorasListState()
    object Empty : MejorasListState()
    data class Success(val data: List<MejoraItemDto>) : MejorasListState()
    data class Error(val message: String) : MejorasListState()
}
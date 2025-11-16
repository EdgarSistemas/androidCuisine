package com.intellisoft.androidcuisine.views.horario

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.intellisoft.androidcuisine.data.remote.dto.HorarioDetalle
import com.intellisoft.androidcuisine.domain.repository.horario.HorarioRepositoryImpl
import com.intellisoft.androidcuisine.domain.repository.horario.HorarioRepository
import com.intellisoft.androidcuisine.util.SessionManager
import kotlinx.coroutines.launch

class HorariosViewModel(application: Application) : AndroidViewModel(application) {

    private val horarioRepository: HorarioRepository = HorarioRepositoryImpl()
    private val sessionManager = SessionManager.getInstance(application)

    private val _horarioState = MutableLiveData<HorarioState>()
    val horarioState: LiveData<HorarioState> = _horarioState

    fun loadHorarioUsuario() {
        val userId = sessionManager.getUserId()

        if (userId == -1) {
            _horarioState.value = HorarioState.Error("Usuario no encontrado")
            return
        }

        _horarioState.value = HorarioState.Loading

        viewModelScope.launch {
            val result = horarioRepository.getHorarioUsuario(userId)

            result.fold(
                onSuccess = { data ->
                    if (data.detalles.isEmpty()) {
                        _horarioState.value = HorarioState.Empty
                    } else {
                        _horarioState.value = HorarioState.Success(data.detalles)
                    }
                },
                onFailure = { exception ->
                    _horarioState.value = HorarioState.Error(exception.message ?: "Error desconocido")
                }
            )
        }
    }
}

sealed class HorarioState {
    object Loading : HorarioState()
    object Empty : HorarioState()
    data class Success(val detalles: List<HorarioDetalle>) : HorarioState()
    data class Error(val message: String) : HorarioState()
}
package com.intellisoft.androidcuisine.views.Main.cuenta

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.util.copy
import com.google.firebase.firestore.auth.User
import com.intellisoft.androidcuisine.MyApplication
import com.intellisoft.androidcuisine.data.remote.dto.UserDto
import com.intellisoft.androidcuisine.data.remote.dto.UsuarioUpdateRequest
import com.intellisoft.androidcuisine.domain.repository.usuario.UsuarioRepository
import com.intellisoft.androidcuisine.domain.repository.usuario.UsuarioRepositoryImpl
import com.intellisoft.androidcuisine.util.SessionManager
import kotlinx.coroutines.launch

class CuentaViewModel : ViewModel() {

    private val repository: UsuarioRepository = UsuarioRepositoryImpl()
    private val sessionManager = SessionManager(MyApplication.instance)

    private val _updateState = MutableLiveData<UpdateState>()
    val updateState: LiveData<UpdateState> = _updateState

    private val _currentUser = MutableLiveData<UserDto?>() // User puede ser nulo si no hay sesión
    val currentUser: LiveData<UserDto?> = _currentUser

    init {
        loadCurrentUser()
    }

    fun loadCurrentUser() {
        _currentUser.value =
            sessionManager.getUserData() as UserDto? // O .getUser() dependiendo de tu SessionManager
    }

    fun updateUsuario(nombre: String, apellido: String, email: String) {
        val userId = sessionManager.getUserId()
        if (userId == 0 || userId == -1) {
            _updateState.value = UpdateState.Error("Error de sesión. ID no encontrado.")
            return
        }

        if (nombre.isBlank() || apellido.isBlank() || email.isBlank()) {
            _updateState.value = UpdateState.Error("Nombre, Apellido y Email son requeridos.")
            return
        }

        _updateState.value = UpdateState.Loading
        val request = UsuarioUpdateRequest(nombre, apellido, email)

        viewModelScope.launch {
            val result = repository.updateUsuario(userId, request)
            result.fold(
                onSuccess = { response ->
                    // 1. Actualizar localmente
                    // Asegúrate de que esta función exista en tu SessionManager (la que creamos antes)
                    sessionManager.updateUserData(nombre, apellido, email)

                    // 2. Actualizar LiveData para refrescar la UI
                    _currentUser.value = _currentUser.value?.copy(
                        nombre = nombre,
                        apellido = apellido,
                        email = email
                    )

                    _updateState.value = UpdateState.Success(response.message)
                },
                onFailure = { exception ->
                    _updateState.value = UpdateState.Error(exception.message ?: "Error de red")
                }
            )
        }
    }

    fun resetState() {
        _updateState.value = UpdateState.Idle
    }

    // TODO: Implementar lógica para cambiarPassword(currentPass, newPass) cuando tengas el endpoint listo
}

sealed class UpdateState {
    object Idle : UpdateState()
    object Loading : UpdateState()
    data class Success(val message: String) : UpdateState()
    data class Error(val message: String) : UpdateState()
}
package com.intellisoft.androidcuisine.views.Cuenta

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.intellisoft.androidcuisine.data.remote.dto.UpdateUsuarioDto // IMPORTANTE: Usar el nuevo DTO
import com.intellisoft.androidcuisine.domain.repository.auth.AuthRepository
import com.intellisoft.androidcuisine.domain.repository.auth.AuthRepositoryImpl
import com.intellisoft.androidcuisine.domain.repository.usuario.UsuarioRepository
import com.intellisoft.androidcuisine.domain.repository.usuario.UsuarioRepositoryImpl
import com.intellisoft.androidcuisine.util.SessionManager
import com.intellisoft.androidcuisine.util.UserData
import kotlinx.coroutines.launch

class CuentaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UsuarioRepository = UsuarioRepositoryImpl()
    private val sessionManager = SessionManager.getInstance(application)
    private val authRepository: AuthRepository = AuthRepositoryImpl()

    private val _updateState = MutableLiveData<UpdateState>()
    val updateState: LiveData<UpdateState> = _updateState

    private val _currentUser = MutableLiveData<UserData?>()
    val currentUser: LiveData<UserData?> = _currentUser

    private val _passwordState = MutableLiveData<UpdateState>()
    val passwordState: LiveData<UpdateState> = _passwordState

    init {
        loadCurrentUser()
    }

    fun loadCurrentUser() {
        _currentUser.value = sessionManager.getUserData()
    }

    fun updateUsuario(nombre: String, apellido: String, email: String) {
        val userId = sessionManager.getUserId()

        if (userId == 0 || userId == -1) {
            _updateState.value = UpdateState.Error("Error de sesión. ID no encontrado.")
            return
        }

        if (nombre.isBlank() || apellido.isBlank() || email.isBlank()) {
            _updateState.value = UpdateState.Error("Todos los campos son obligatorios.")
            return
        }

        if (nombre.length > 50) {
            _updateState.value = UpdateState.Error("El nombre es demasiado largo (máx 50 caracteres).")
            return
        }
        if (apellido.length > 30) {
            _updateState.value = UpdateState.Error("El apellido es demasiado largo (máx 30 caracteres).")
            return
        }
        if (email.length > 50) {
            _updateState.value = UpdateState.Error("El correo es demasiado largo (máx 50 caracteres).")
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _updateState.value = UpdateState.Error("El formato del correo no es válido.")
            return
        }

        _updateState.value = UpdateState.Loading

        // CORRECCIÓN: Usamos UpdateUsuarioDto en lugar de UsuarioUpdateRequest
        val request = UpdateUsuarioDto(nombre, apellido, email)

        viewModelScope.launch {
            val result = repository.updateUsuario(userId, request)
            result.fold(
                onSuccess = { response -> // response es UsuarioDto (el usuario actualizado)
                    // Actualizar sesión local
                    sessionManager.updateUserData(nombre, apellido, email)

                    val currentUser = _currentUser.value
                    if (currentUser != null) {
                        _currentUser.value = currentUser.copy(
                            nombre = nombre,
                            apellido = apellido,
                            email = email
                        )
                    }

                    // CORRECCIÓN: UsuarioDto no tiene 'message'. Ponemos un mensaje manual.
                    _updateState.value = UpdateState.Success("Perfil actualizado correctamente")
                },
                onFailure = { exception ->
                    _updateState.value = UpdateState.Error(exception.message ?: "Error de conexión")
                }
            )
        }
    }

    fun cambiarPassword(actual: String, nueva: String, confirmacion: String) {
        if (actual.isBlank() || nueva.isBlank() || confirmacion.isBlank()) {
            _passwordState.value = UpdateState.Error("Todos los campos son obligatorios.")
            return
        }
        if (nueva != confirmacion) {
            _passwordState.value = UpdateState.Error("Las contraseñas nuevas no coinciden.")
            return
        }
        if (nueva.length < 6) {
            _passwordState.value = UpdateState.Error("La contraseña debe tener al menos 6 caracteres.")
            return
        }

        _passwordState.value = UpdateState.Loading

        viewModelScope.launch {
            val result = authRepository.cambiarPassword(actual, nueva, confirmacion)
            result.fold(
                onSuccess = { response ->
                    // Asumimos que authRepository devuelve un objeto que SÍ tiene message, o String
                    _passwordState.value = UpdateState.Success(response.message ?: "Contraseña actualizada")
                },
                onFailure = { exception ->
                    val msg = exception.message ?: "Error desconocido"
                    _passwordState.value = UpdateState.Error(msg)
                }
            )
        }
    }

    fun resetPasswordState() {
        _passwordState.value = UpdateState.Idle
    }

    fun resetState() {
        _updateState.value = UpdateState.Idle
    }
}

sealed class UpdateState {
    object Idle : UpdateState()
    object Loading : UpdateState()
    data class Success(val message: String) : UpdateState()
    data class Error(val message: String) : UpdateState()
}
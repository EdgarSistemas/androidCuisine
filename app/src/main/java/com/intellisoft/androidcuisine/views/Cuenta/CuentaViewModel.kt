package com.intellisoft.androidcuisine.views.Main.cuenta

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.intellisoft.androidcuisine.data.remote.dto.UsuarioUpdateRequest
import com.intellisoft.androidcuisine.domain.repository.usuario.UsuarioRepository
import com.intellisoft.androidcuisine.domain.repository.usuario.UsuarioRepositoryImpl
import com.intellisoft.androidcuisine.util.SessionManager
import com.intellisoft.androidcuisine.util.UserData // <--- IMPORTANTE: Importar UserData
import kotlinx.coroutines.launch

class CuentaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UsuarioRepository = UsuarioRepositoryImpl()
    private val sessionManager = SessionManager.getInstance(application)

    private val _updateState = MutableLiveData<UpdateState>()
    val updateState: LiveData<UpdateState> = _updateState

    // CAMBIO 1: Usar UserData en lugar de UserDto
    private val _currentUser = MutableLiveData<UserData?>()
    val currentUser: LiveData<UserData?> = _currentUser

    init {
        loadCurrentUser()
    }

    fun loadCurrentUser() {
        // CAMBIO 2: Asignar directamente sin casting
        _currentUser.value = sessionManager.getUserData()
    }

    fun updateUsuario(nombre: String, apellido: String, email: String) {
        val userId = sessionManager.getUserId()


        if (userId == 0 || userId == -1) {
            _updateState.value = UpdateState.Error("Error de sesión. ID no encontrado.")
            return
        }

        if (nombre.isBlank()) {
            _updateState.value = UpdateState.Error("El nombre es obligatorio.")
            return
        }
        if (apellido.isBlank()) {
            _updateState.value = UpdateState.Error("El apellido es obligatorio.")
            return
        }
        if (email.isBlank()) {
            _updateState.value = UpdateState.Error("El correo es obligatorio.")
            return
        }

        // 2. Validaciones de Longitud (Basado en database-schema.sql)
        if (nombre.length > 50) {
            _updateState.value = UpdateState.Error("El nombre es demasiado largo (máx 50 caracteres).")
            return
        }
        if (apellido.length > 30) {
            _updateState.value = UpdateState.Error("El apellido es demasiado largo (máx 30 caracteres).")
            return
        }
        if (email.length > 50) {
            // OJO: 50 caracteres para un email puede ser poco para algunos usuarios corporativos,
            // pero es lo que manda tu base de datos actual.
            _updateState.value = UpdateState.Error("El correo es demasiado largo (máx 50 caracteres).")
            return
        }

        // 3. Validación de Formato de Email
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _updateState.value = UpdateState.Error("El formato del correo no es válido.")
            return
        }

        // Si pasa todas las validaciones, procedemos
        _updateState.value = UpdateState.Loading
        val request = UsuarioUpdateRequest(nombre, apellido, email)

        viewModelScope.launch {
            val result = repository.updateUsuario(userId, request)
            result.fold(
                onSuccess = { response ->
                    // Actualizar sesión local
                    sessionManager.updateUserData(nombre, apellido, email)

                    // Actualizar LiveData para refrescar la UI
                    val currentUser = _currentUser.value
                    if (currentUser != null) {
                        // .copy funciona porque UserData es un data class
                        _currentUser.value = currentUser.copy(
                            nombre = nombre,
                            apellido = apellido,
                            email = email
                        )
                    }

                    _updateState.value = UpdateState.Success(response.message)
                },
                onFailure = { exception ->
                    _updateState.value = UpdateState.Error(exception.message ?: "Error de conexión")
                }
            )
        }
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
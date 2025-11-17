// edgarsistemas/androidcuisine/androidCuisine-dev-cesar/app/src/main/java/com/intellisoft/androidcuisine/views/auth/RecuperacionViewModel.kt
package com.intellisoft.androidcuisine.views.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intellisoft.androidcuisine.domain.repository.recuperacion.recuperacionRepository
import com.intellisoft.androidcuisine.domain.repository.recuperacion.recuperacionRepositoryImpl
import kotlinx.coroutines.launch

class RecuperacionViewModel : ViewModel() {

    private val repository: recuperacionRepository = recuperacionRepositoryImpl()

    private val _solicitudState = MutableLiveData<SolicitudState>()
    val solicitudState: LiveData<SolicitudState> = _solicitudState

    private val _verificacionState = MutableLiveData<VerificacionState>()
    val verificacionState: LiveData<VerificacionState> = _verificacionState

    private val _restablecerState = MutableLiveData<RestablecerState>()
    val restablecerState: LiveData<RestablecerState> = _restablecerState

    // 1. Solicitar el código de recuperación
    fun solicitarCodigo(email: String) {
        if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _solicitudState.value = SolicitudState.Error("Introduce un correo electrónico válido.")
            return
        }

        _solicitudState.value = SolicitudState.Loading

        viewModelScope.launch {
            val result = repository.postSolicitarCodigo(email)

            result.fold(
                onSuccess = { success ->
                    if (success) {
                        _solicitudState.value = SolicitudState.Success(email)
                    } else {
                        // Aunque el endpoint devuelve Result<Boolean>, el repository asegura que body.success sea true
                        _solicitudState.value = SolicitudState.Error("Error desconocido al solicitar el código.")
                    }
                },
                onFailure = { exception ->
                    _solicitudState.value = SolicitudState.Error(exception.message ?: "Error de red o correo no encontrado.")
                }
            )
        }
    }

    // 2. Verificar el código de recuperación
    fun verificarCodigo(codigo: String, email: String) {
        if (codigo.isBlank() || codigo.length != 6) {
            _verificacionState.value = VerificacionState.Error("El código debe tener 6 dígitos.")
            return
        }

        _verificacionState.value = VerificacionState.Loading

        viewModelScope.launch {
            val result = repository.postVerificarSolicitud(codigo, email)

            result.fold(
                onSuccess = { message ->
                    _verificacionState.value = VerificacionState.Success(message, email, codigo)
                },
                onFailure = { exception ->
                    _verificacionState.value = VerificacionState.Error(exception.message ?: "Código incorrecto o expirado.")
                }
            )
        }
    }

    // 3. Restablecer la contraseña
    fun restablecerPassword(codigo: String, email: String, nuevaPassword: String, confirmarPassword: String) {
        if (nuevaPassword.isBlank() || nuevaPassword.length < 6) {
            _restablecerState.value = RestablecerState.Error("La contraseña debe tener al menos 6 caracteres.")
            return
        }

        if (nuevaPassword != confirmarPassword) {
            _restablecerState.value = RestablecerState.Error("Las contraseñas no coinciden.")
            return
        }

        _restablecerState.value = RestablecerState.Loading

        viewModelScope.launch {
            val result = repository.postRestablecerSolicitud(codigo, email, nuevaPassword)

            result.fold(
                onSuccess = { response ->
                    _restablecerState.value = RestablecerState.Success(response.message)
                },
                onFailure = { exception ->
                    _restablecerState.value = RestablecerState.Error(exception.message ?: "Error al restablecer la contraseña.")
                }
            )
        }
    }

    // Limpiar estados
    fun resetSolicitudState() { _solicitudState.value = SolicitudState.Idle }
    fun resetVerificacionState() { _verificacionState.value = VerificacionState.Idle }
}

sealed class SolicitudState {
    object Idle : SolicitudState()
    object Loading : SolicitudState()
    data class Success(val email: String) : SolicitudState()
    data class Error(val message: String) : SolicitudState()
}

sealed class VerificacionState {
    object Idle : VerificacionState()
    object Loading : VerificacionState()
    data class Success(val message: String, val email: String, val codigo: String) : VerificacionState()
    data class Error(val message: String) : VerificacionState()
}

sealed class RestablecerState {
    object Idle : RestablecerState()
    object Loading : RestablecerState()
    data class Success(val message: String) : RestablecerState()
    data class Error(val message: String) : RestablecerState()
}
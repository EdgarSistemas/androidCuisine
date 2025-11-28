package com.intellisoft.androidcuisine.views.usuarios

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intellisoft.androidcuisine.data.remote.dto.*
import com.intellisoft.androidcuisine.domain.repository.usuario.UsuarioRepository
import com.intellisoft.androidcuisine.domain.repository.usuario.UsuarioRepositoryImpl
import kotlinx.coroutines.launch

class UsuariosViewModel : ViewModel() {

    // Inicializamos el repositorio
    private val repository: UsuarioRepository = UsuarioRepositoryImpl()

    // LiveData para la lista de usuarios
    private val _usuarios = MutableLiveData<List<UsuarioDto>>()
    val usuarios: LiveData<List<UsuarioDto>> = _usuarios

    // LiveData para el dropdown de roles
    private val _roles = MutableLiveData<List<RolDto>>()
    val roles: LiveData<List<RolDto>> = _roles

    // Estados de UI
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message

    private val _operationSuccess = MutableLiveData<Boolean>()
    val operationSuccess: LiveData<Boolean> = _operationSuccess

    fun cargarDatosIniciales(sucursalId: Int) {
        cargarRoles()
        listarUsuarios(sucursalId)
    }

    fun listarUsuarios(sucursalId: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            // Filtramos por sucursal, rolId null para traer todos los de esa sucursal
            val result = repository.getUsuarios(rolId = null, sucursalId = sucursalId)

            result.onSuccess { list ->
                _usuarios.value = list
            }.onFailure { e ->
                _message.value = "Error al cargar usuarios: ${e.message}"
            }
            _isLoading.value = false
        }
    }

    private fun cargarRoles() {
        viewModelScope.launch {
            val result = repository.getRoles()
            result.onSuccess { list ->
                _roles.value = list
            }
            // No mostramos error en roles para no bloquear la UI, pero podrías logearlo
        }
    }

    fun crearUsuario(req: UsuarioCreateRequest) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = repository.createUsuario(req)

            result.onSuccess {
                _message.value = "Usuario creado exitosamente"
                _operationSuccess.value = true
            }.onFailure { e ->
                _message.value = "Error al crear: ${e.message}"
            }
            _isLoading.value = false
        }
    }

    fun actualizarUsuario(id: Int, req: UsuarioUpdateRequest) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = repository.updateUsuario(id, req)

            result.onSuccess {
                _message.value = "Usuario actualizado correctamente"
                _operationSuccess.value = true
            }.onFailure { e ->
                _message.value = "Error al actualizar: ${e.message}"
            }
            _isLoading.value = false
        }
    }

    fun eliminarUsuario(id: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = repository.deleteUsuario(id)

            result.onSuccess {
                _message.value = "Usuario eliminado correctamente"
                _operationSuccess.value = true
            }.onFailure { e ->
                _message.value = "Error al eliminar: ${e.message}"
            }
            _isLoading.value = false
        }
    }

    fun clearMessage() {
        _message.value = null
    }

    fun resetOperation() {
        _operationSuccess.value = false
    }
}
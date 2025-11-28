package com.intellisoft.androidcuisine.views.Usuarios

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intellisoft.androidcuisine.data.remote.dto.CreateUsuarioDto
import com.intellisoft.androidcuisine.data.remote.dto.RolItemDto
import com.intellisoft.androidcuisine.data.remote.dto.UpdateUsuarioDto
import com.intellisoft.androidcuisine.data.remote.dto.UsuarioDto
import com.intellisoft.androidcuisine.domain.repository.usuario.UsuarioRepository
import com.intellisoft.androidcuisine.util.SessionManager
import kotlinx.coroutines.launch

class UsuariosViewModel(
    private val repository: UsuarioRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    // ============================================================================================
    //  ESTADOS (LIVEDATA)
    // ============================================================================================

    private val _usuarios = MutableLiveData<List<UsuarioDto>>()
    val usuarios: LiveData<List<UsuarioDto>> = _usuarios

    private val _roles = MutableLiveData<List<RolItemDto>>()
    val roles: LiveData<List<RolItemDto>> = _roles

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _mensaje = MutableLiveData<String>()
    val mensaje: LiveData<String> = _mensaje

    private val _operacionExitosa = MutableLiveData<Boolean>()
    val operacionExitosa: LiveData<Boolean> = _operacionExitosa

    var rolSeleccionadoId: Int = 0

    // ============================================================================================
    //  FUNCIONES
    // ============================================================================================

    /**
     * Carga la lista de roles para llenar el DropDown (Spinner).
     */
    fun cargarRoles() {
        viewModelScope.launch {
            repository.getRoles()
                .onSuccess { _roles.value = it }
                .onFailure { _mensaje.value = "Error cargando roles: ${it.message}" }
        }
    }

    fun cargarDatosIniciales() {
        val sucursalId = sessionManager.getSucursalId()

        // 1. VALIDACIÓN DE SUCURSAL
        if (sucursalId <= 0) {
            _mensaje.value = "⚠️ No has seleccionado una sucursal de trabajo."
            return
        }

        // 2. CARGAR ROLES PRIMERO
        _isLoading.value = true
        viewModelScope.launch {
            repository.getRoles()
                .onSuccess { listaRoles ->
                    _roles.value = listaRoles

                    // 3. AUTORRUTINA: Si hay roles, cargamos los usuarios del PRIMERO por defecto
                    if (listaRoles.isNotEmpty()) {
                        val primerRol = listaRoles[0]
                        rolSeleccionadoId = primerRol.id
                        listarUsuarios(primerRol.id) // <--- Aquí ocurre la magia
                    } else {
                        _isLoading.value = false
                        _mensaje.value = "No se encontraron roles en el sistema."
                    }
                }
                .onFailure {
                    _isLoading.value = false
                    _mensaje.value = "Error al cargar roles: ${it.message}"
                }
        }
    }
    /**
     * Lista los usuarios de la sucursal actual.
     * Si el usuario es Admin, listará los de la sucursal que haya seleccionado.
     */
    fun listarUsuarios(rolId: Int) {
        val sucursalId = sessionManager.getSucursalId()

        if (sucursalId <= 0) return // Ya validado arriba, pero por seguridad

        _isLoading.value = true
        viewModelScope.launch {
            // Enviamos el rolId específico que el backend exige
            repository.getUsuarios(rolId = rolId, sucursalId = sucursalId)
                .onSuccess {
                    _usuarios.value = it
                    _isLoading.value = false
                }
                .onFailure {
                    _mensaje.value = "Error al listar: ${it.message}"
                    _isLoading.value = false
                }
        }
    }

    /**
     * Crea un nuevo empleado.
     */
    fun crearUsuario(nombre: String, apellido: String, email: String, pass: String, tel: String, rolId: Int) {
        _isLoading.value = true
        val sucursalId = sessionManager.getSucursalId() // Asignamos a la sucursal actual


        val dto = CreateUsuarioDto(nombre, apellido, email, pass, tel, rolId, sucursalId)

        viewModelScope.launch {
            repository.createUsuario(dto).onSuccess {
                _operacionExitosa.value = true
                listarUsuarios(rolSeleccionadoId) // Recargar la lista del rol actual
            }.onFailure { _mensaje.value = it.message }
        }
    }

    /**
     * Actualiza un usuario existente.
     */
    fun actualizarUsuario(id: Int, nombre: String, apellido: String, email: String) {
        _isLoading.value = true

        val usuarioEditado = UpdateUsuarioDto(
            nombre = nombre,
            apellido = apellido,
            email = email
        )

        viewModelScope.launch {
            repository.updateUsuario(id, usuarioEditado)
                .onSuccess {
                    _operacionExitosa.value = true
                    _mensaje.value = "Usuario actualizado correctamente"
                    _isLoading.value = false
                }
                .onFailure {
                    _mensaje.value = it.message ?: "Error al actualizar"
                    _isLoading.value = false
                }
        }
    }

    /**
     * Elimina (o desactiva) un usuario.
     */
    fun eliminarUsuario(id: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            repository.deleteUsuario(id)
                .onSuccess {
                    _operacionExitosa.value = true
                    _mensaje.value = "Usuario eliminado"
                    _isLoading.value = false
                }
                .onFailure {
                    _mensaje.value = it.message ?: "Error al eliminar"
                    _isLoading.value = false
                }
        }
    }

    // Opcional: Para limpiar mensajes después de mostrarlos
    fun limpiarMensaje() {
        _mensaje.value = ""
    }

    // Opcional: Para resetear el flag de operación exitosa
    fun resetOperacion() {
        _operacionExitosa.value = false
    }
}
package com.intellisoft.androidcuisine.views.tickets

import TicketRepositoryImpl
import android.app.Application
import android.graphics.Bitmap
import android.util.Base64
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.intellisoft.androidcuisine.data.remote.dto.TicketDto
import com.intellisoft.androidcuisine.util.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class TicketsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TicketRepositoryImpl()
    private val sessionManager = SessionManager.getInstance(application)

    // Estados de la lista
    private val _ticketsState = MutableLiveData<TicketsListState>()
    val ticketsState: LiveData<TicketsListState> = _ticketsState

    // Estados de creación
    private val _createState = MutableLiveData<TicketCreateState>()
    val createState: LiveData<TicketCreateState> = _createState

    // Saber si el usuario es Admin/Gerente para habilitar cambios de estatus
    fun isUserAdminOrManager(): Boolean {
        val role = sessionManager.getPrimaryRole().uppercase()
        return role.contains("ADMIN") || role.contains("GERENTE")
    }

    fun cargarTickets(estatus: Int? = null) {
        _ticketsState.value = TicketsListState.Loading
        viewModelScope.launch {
            val result = repository.getTickets(estatus)
            result.fold(
                onSuccess = { list ->
                    if (list.isEmpty()) _ticketsState.value = TicketsListState.Empty
                    else _ticketsState.value = TicketsListState.Success(list)
                },
                onFailure = { e ->
                    _ticketsState.value = TicketsListState.Error(e.message ?: "Error desconocido")
                }
            )
        }
    }

    fun crearTicket(notas: String, bitmap: Bitmap?) {
        if (notas.isBlank()) {
            _createState.value = TicketCreateState.Error("Debes agregar una descripción.")
            return
        }
        if (bitmap == null) {
            _createState.value = TicketCreateState.Error("La fotografía es obligatoria.")
            return
        }

        _createState.value = TicketCreateState.Loading

        viewModelScope.launch {
            // 1. Convertir imagen en hilo secundario (Default dispatcher)
            val base64String = convertBitmapToBase64(bitmap)

            // 2. Enviar al repositorio
            val result = repository.crearTicket(notas, base64String)

            result.fold(
                onSuccess = {
                    _createState.value = TicketCreateState.Success("Ticket creado correctamente")
                    cargarTickets() // Recargar la lista de fondo
                },
                onFailure = { e ->
                    _createState.value = TicketCreateState.Error(e.message ?: "Error al enviar")
                }
            )
        }
    }

    // Función auxiliar para convertir Bitmap a Base64
    private suspend fun convertBitmapToBase64(bitmap: Bitmap): String = withContext(Dispatchers.Default) {
        val outputStream = ByteArrayOutputStream()
        // Comprimimos a JPEG calidad 60 para no saturar el payload
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, outputStream)
        val byteParams = outputStream.toByteArray()
        // No agregamos prefijos "data:image...", enviamos el raw string
        Base64.encodeToString(byteParams, Base64.NO_WRAP)
    }

    fun resetCreateState() {
        _createState.value = TicketCreateState.Idle
    }
}

sealed class TicketsListState {
    object Loading : TicketsListState()
    object Empty : TicketsListState()
    data class Success(val data: List<TicketDto>) : TicketsListState()
    data class Error(val message: String) : TicketsListState()
}

sealed class TicketCreateState {
    object Idle : TicketCreateState()
    object Loading : TicketCreateState()
    data class Success(val message: String) : TicketCreateState()
    data class Error(val message: String) : TicketCreateState()
}
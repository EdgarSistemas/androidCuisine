import com.intellisoft.androidcuisine.data.remote.api.ApiClient
import com.intellisoft.androidcuisine.data.remote.dto.TicketDto
import com.intellisoft.androidcuisine.data.remote.dto.TicketRequest
import com.intellisoft.androidcuisine.domain.repository.tickets.TicketRepository

class TicketRepositoryImpl : TicketRepository {

    override suspend fun getTickets(estatus: Int?): Result<List<TicketDto>> {
        return try {
            val response = ApiClient.ticketService.getTickets(estatus)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data ?: emptyList())
            } else {
                Result.failure(Exception(response.body()?.message ?: "Error al obtener tickets"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun crearTicket(notas: String, base64Image: String): Result<TicketDto> {
        return try {
            // Construimos el objeto con la imagen ya en Base64
            val request = TicketRequest(notas, base64Image)
            val response = ApiClient.ticketService.createTicket(request)

            if (response.isSuccessful && response.body()?.success == true) {
                // Retornamos el ticket creado si viene en la respuesta, o uno dummy si es null
                Result.success(response.body()!!.data!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: response.message()
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun cambiarEstatus(idTicket: Int, nuevoEstatus: Int): Result<Boolean> {
        return try {
            val body = mapOf("estatus" to nuevoEstatus)
            val response = ApiClient.ticketService.updateTicketEstatus(idTicket, body)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(true)
            } else {
                Result.failure(Exception("Error al actualizar estatus"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
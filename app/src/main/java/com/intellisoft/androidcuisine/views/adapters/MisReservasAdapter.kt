package com.intellisoft.androidcuisine.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.data.remote.dto.ReservaClienteDto

class MisReservasAdapter(
    private val onIniciarClick: (ReservaClienteDto) -> Unit,
    private val onCompletarClick: (ReservaClienteDto) -> Unit,
    private val onCancelarClick: (ReservaClienteDto) -> Unit,
    private val onOrdenarClick: (ReservaClienteDto) -> Unit  // NUEVO
) : ListAdapter<ReservaClienteDto, MisReservasAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reserva_cliente, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val card: MaterialCardView = itemView.findViewById(R.id.cardReserva)
        private val tvIdReserva: TextView = itemView.findViewById(R.id.tvIdReserva)
        private val chipEstatus: Chip = itemView.findViewById(R.id.chipEstatus)
        private val tvFecha: TextView = itemView.findViewById(R.id.tvFecha)
        private val tvHorario: TextView = itemView.findViewById(R.id.tvHorario)
        private val tvNotas: TextView = itemView.findViewById(R.id.tvNotas)
        private val btnIniciar: MaterialButton = itemView.findViewById(R.id.btnIniciar)
        private val btnCompletar: MaterialButton = itemView.findViewById(R.id.btnCompletar)
        private val btnCancelar: MaterialButton = itemView.findViewById(R.id.btnCancelar)
        private val btnOrdenar: MaterialButton = itemView.findViewById(R.id.btnOrdenar)  // NUEVO

        fun bind(reserva: ReservaClienteDto) {
            val context = itemView.context

            tvIdReserva.text = "Reserva #${reserva.id_reserva}"

            // Estatus con colores
            chipEstatus.text = reserva.estatus_display ?: getEstatusDisplay(reserva.estatus)
            when (reserva.estatus) {
                1 -> {
                    chipEstatus.setChipBackgroundColorResource(R.color.cuisine_blue_light)
                    chipEstatus.setTextColor(ContextCompat.getColor(context, R.color.cuisine_blue))
                }
                2 -> {
                    chipEstatus.setChipBackgroundColorResource(R.color.cuisine_green_light)
                    chipEstatus.setTextColor(ContextCompat.getColor(context, R.color.cuisine_green_dark))
                }
                3 -> {
                    chipEstatus.setChipBackgroundColorResource(R.color.cuisine_gray_light)
                    chipEstatus.setTextColor(ContextCompat.getColor(context, R.color.cuisine_gray))
                }
                4 -> {
                    chipEstatus.setChipBackgroundColorResource(R.color.cuisine_yellow_light)
                    chipEstatus.setTextColor(ContextCompat.getColor(context, R.color.cuisine_yellow_dark))
                }
                5 -> {
                    chipEstatus.setChipBackgroundColorResource(R.color.cuisine_red_light)
                    chipEstatus.setTextColor(ContextCompat.getColor(context, R.color.cuisine_red))
                }
            }

            // Fecha y horario
            val fecha = reserva.inicio.take(10)
            val horaInicio = if (reserva.inicio.length >= 16) {
                reserva.inicio.substring(11, 16)
            } else ""
            val horaFin = if (reserva.fin_estimado.length >= 16) {
                reserva.fin_estimado.substring(11, 16)
            } else ""

            tvFecha.text = "📅 $fecha"
            tvHorario.text = "🕐 $horaInicio - $horaFin"

            // Notas
            if (reserva.notas.isNullOrEmpty()) {
                tvNotas.visibility = View.GONE
            } else {
                tvNotas.visibility = View.VISIBLE
                tvNotas.text = "📝 ${reserva.notas}"
            }

            // Ocultar todos los botones
            btnIniciar.visibility = View.GONE
            btnCompletar.visibility = View.GONE
            btnCancelar.visibility = View.GONE
            btnOrdenar.visibility = View.GONE

            // Mostrar botones según estatus
            when (reserva.estatus) {
                1 -> { // Programada
                    btnIniciar.visibility = View.VISIBLE
                    btnCancelar.visibility = View.VISIBLE

                    if (reserva.puede_iniciar == true) {
                        btnIniciar.isEnabled = true
                        btnIniciar.alpha = 1f
                    } else {
                        btnIniciar.isEnabled = true
                        btnIniciar.alpha = 0.7f
                    }
                }
                2 -> { // En Curso
                    btnOrdenar.visibility = View.VISIBLE  // NUEVO - Mostrar botón ordenar
                    btnCompletar.visibility = View.VISIBLE
                    btnCancelar.visibility = View.VISIBLE
                }
            }

            // Click listeners
            btnIniciar.setOnClickListener { onIniciarClick(reserva) }
            btnCompletar.setOnClickListener { onCompletarClick(reserva) }
            btnCancelar.setOnClickListener { onCancelarClick(reserva) }
            btnOrdenar.setOnClickListener { onOrdenarClick(reserva) }  // NUEVO
        }

        private fun getEstatusDisplay(estatus: Int?): String {
            return when (estatus) {
                1 -> "Programada"
                2 -> "En Curso"
                3 -> "Completada"
                4 -> "No Show"
                5 -> "Cancelada"
                else -> "Desconocido"
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ReservaClienteDto>() {
        override fun areItemsTheSame(oldItem: ReservaClienteDto, newItem: ReservaClienteDto): Boolean {
            return oldItem.id_reserva == newItem.id_reserva
        }

        override fun areContentsTheSame(oldItem: ReservaClienteDto, newItem: ReservaClienteDto): Boolean {
            return oldItem == newItem
        }
    }
}
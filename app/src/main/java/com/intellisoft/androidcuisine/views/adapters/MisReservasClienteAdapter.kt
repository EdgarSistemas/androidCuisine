package com.intellisoft.androidcuisine.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.data.remote.dto.ReservaClienteDto
import java.text.SimpleDateFormat
import java.util.*

class MisReservasClienteAdapter(
    private val onCancelar: (ReservaClienteDto) -> Unit
) : ListAdapter<ReservaClienteDto, MisReservasClienteAdapter.ReservaViewHolder>(ReservaDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reserva_cliente, parent, false)
        return ReservaViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReservaViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ReservaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardReserva: MaterialCardView = itemView.findViewById(R.id.cardReserva)
        private val tvFecha: TextView = itemView.findViewById(R.id.tvFecha)
        private val tvHora: TextView = itemView.findViewById(R.id.tvHora)
        private val chipEstado: Chip = itemView.findViewById(R.id.chipEstado)
        private val tvMesa: TextView = itemView.findViewById(R.id.tvMesa)
        private val tvSucursal: TextView = itemView.findViewById(R.id.tvSucursal)
        private val tvNotas: TextView = itemView.findViewById(R.id.tvNotas)
        private val btnCancelar: MaterialButton = itemView.findViewById(R.id.btnCancelar)
        private val layoutFecha: View = itemView.findViewById(R.id.layoutFecha)

        fun bind(reserva: ReservaClienteDto) {
            val (fecha, hora) = formatearFechaHora(reserva.inicio)
            tvFecha.text = fecha
            tvHora.text = hora

            tvMesa.text = reserva.codigo_mesa ?: "Mesa ${reserva.mesa_id}"

            if (!reserva.sucursal_nombre.isNullOrEmpty()) {
                tvSucursal.text = reserva.sucursal_nombre
                tvSucursal.visibility = View.VISIBLE
            } else {
                tvSucursal.visibility = View.GONE
            }

            if (!reserva.notas.isNullOrEmpty()) {
                tvNotas.text = reserva.notas
                tvNotas.visibility = View.VISIBLE
            } else {
                tvNotas.visibility = View.GONE
            }

            configurarEstado(reserva)
        }

        private fun configurarEstado(reserva: ReservaClienteDto) {
            val estadoTexto = reserva.estado_descripcion ?: getEstadoTexto(reserva.estado)
            chipEstado.text = estadoTexto

            when (reserva.estado) {
                0 -> {
                    chipEstado.setChipBackgroundColorResource(R.color.cuisine_green_light)
                    chipEstado.setTextColor(itemView.context.getColor(R.color.cuisine_dark_text))
                    layoutFecha.setBackgroundResource(R.color.cuisine_green_dark)
                    btnCancelar.visibility = View.VISIBLE
                    btnCancelar.setOnClickListener { onCancelar(reserva) }
                    cardReserva.alpha = 1f
                }
                1 -> {
                    chipEstado.setChipBackgroundColorResource(R.color.cuisine_green_dark)
                    chipEstado.setTextColor(itemView.context.getColor(R.color.white))
                    layoutFecha.setBackgroundResource(R.color.cuisine_green_dark)
                    btnCancelar.visibility = View.GONE
                    cardReserva.alpha = 1f
                }
                2 -> {
                    chipEstado.setChipBackgroundColorResource(android.R.color.darker_gray)
                    chipEstado.setTextColor(itemView.context.getColor(R.color.white))
                    layoutFecha.setBackgroundColor(itemView.context.getColor(android.R.color.darker_gray))
                    btnCancelar.visibility = View.GONE
                    cardReserva.alpha = 0.7f
                }
                3 -> {
                    chipEstado.setChipBackgroundColorResource(android.R.color.holo_red_light)
                    chipEstado.setTextColor(itemView.context.getColor(R.color.white))
                    layoutFecha.setBackgroundColor(itemView.context.getColor(android.R.color.holo_red_dark))
                    btnCancelar.visibility = View.GONE
                    cardReserva.alpha = 0.5f
                }
                4 -> {
                    chipEstado.setChipBackgroundColorResource(android.R.color.holo_orange_dark)
                    chipEstado.setTextColor(itemView.context.getColor(R.color.white))
                    layoutFecha.setBackgroundColor(itemView.context.getColor(android.R.color.holo_orange_dark))
                    btnCancelar.visibility = View.GONE
                    cardReserva.alpha = 0.5f
                }
            }
        }

        private fun getEstadoTexto(estado: Int): String {
            return when (estado) {
                0 -> "Confirmada"
                1 -> "En curso"
                2 -> "Completada"
                3 -> "Cancelada"
                4 -> "No llegó"
                else -> "Desconocido"
            }
        }

        private fun formatearFechaHora(fechaHora: String): Pair<String, String> {
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val dateFormat = SimpleDateFormat("EEE, dd MMM", Locale("es", "MX"))
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

                val date = inputFormat.parse(fechaHora)
                if (date != null) {
                    Pair(dateFormat.format(date), timeFormat.format(date))
                } else {
                    extraerFechaHoraManual(fechaHora)
                }
            } catch (e: Exception) {
                try {
                    val inputFormat2 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    val dateFormat = SimpleDateFormat("EEE, dd MMM", Locale("es", "MX"))
                    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

                    val date = inputFormat2.parse(fechaHora)
                    if (date != null) {
                        Pair(dateFormat.format(date), timeFormat.format(date))
                    } else {
                        extraerFechaHoraManual(fechaHora)
                    }
                } catch (e2: Exception) {
                    extraerFechaHoraManual(fechaHora)
                }
            }
        }

        private fun extraerFechaHoraManual(fechaHora: String): Pair<String, String> {
            return try {
                val fecha = fechaHora.substring(0, 10)
                val hora = if (fechaHora.length >= 16) fechaHora.substring(11, 16) else "00:00"
                Pair(fecha, hora)
            } catch (e: Exception) {
                Pair(fechaHora, "")
            }
        }
    }

    class ReservaDiffCallback : DiffUtil.ItemCallback<ReservaClienteDto>() {
        override fun areItemsTheSame(oldItem: ReservaClienteDto, newItem: ReservaClienteDto): Boolean {
            return oldItem.id_reserva == newItem.id_reserva
        }

        override fun areContentsTheSame(oldItem: ReservaClienteDto, newItem: ReservaClienteDto): Boolean {
            return oldItem == newItem
        }
    }
}
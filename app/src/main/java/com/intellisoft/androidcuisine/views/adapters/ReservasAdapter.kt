package com.intellisoft.androidcuisine.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.data.remote.dto.ReservaDto
import java.text.SimpleDateFormat
import java.util.*

class ReservasAdapter(
    private val esCliente: Boolean = false,
    private val onIniciar: (ReservaDto) -> Unit,
    private val onCompletar: (ReservaDto) -> Unit,
    private val onCancelar: (ReservaDto) -> Unit,
    private val onNoShow: (ReservaDto) -> Unit
) : ListAdapter<ReservaDto, ReservasAdapter.ReservaViewHolder>(ReservaDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reserva, parent, false)
        return ReservaViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReservaViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ReservaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvHora: TextView = itemView.findViewById(R.id.tvHora)
        private val chipEstatus: Chip = itemView.findViewById(R.id.chipEstatus)
        private val tvMesa: TextView = itemView.findViewById(R.id.tvMesa)
        private val tvNotas: TextView = itemView.findViewById(R.id.tvNotas)
        private val layoutAcciones: LinearLayout = itemView.findViewById(R.id.layoutAcciones)
        private val btnNoShow: MaterialButton = itemView.findViewById(R.id.btnNoShow)
        private val btnCancelar: MaterialButton = itemView.findViewById(R.id.btnCancelar)
        private val btnAccion: MaterialButton = itemView.findViewById(R.id.btnAccion)

        fun bind(reserva: ReservaDto) {
            tvHora.text = formatHora(reserva.inicio)
            tvMesa.text = reserva.codigo_mesa ?: "Mesa ${reserva.mesa_id ?: "-"}"

            if (!reserva.notas.isNullOrEmpty()) {
                tvNotas.text = reserva.notas
                tvNotas.visibility = View.VISIBLE
            } else {
                tvNotas.visibility = View.GONE
            }

            configurarEstatus(reserva)
            configurarBotones(reserva)
        }

        private fun configurarEstatus(reserva: ReservaDto) {
            chipEstatus.text = reserva.estatus_display

            when (reserva.estatus) {
                1 -> {
                    chipEstatus.setChipBackgroundColorResource(R.color.cuisine_green_light)
                    chipEstatus.setTextColor(itemView.context.getColor(R.color.cuisine_dark_text))
                }
                2 -> {
                    chipEstatus.setChipBackgroundColorResource(R.color.cuisine_green_dark)
                    chipEstatus.setTextColor(itemView.context.getColor(R.color.white))
                }
                3 -> {
                    chipEstatus.setChipBackgroundColorResource(android.R.color.darker_gray)
                    chipEstatus.setTextColor(itemView.context.getColor(R.color.white))
                }
                4 -> {
                    chipEstatus.setChipBackgroundColorResource(android.R.color.holo_red_dark)
                    chipEstatus.setTextColor(itemView.context.getColor(R.color.white))
                }
                5 -> {
                    chipEstatus.setChipBackgroundColorResource(android.R.color.holo_orange_dark)
                    chipEstatus.setTextColor(itemView.context.getColor(R.color.white))
                }
            }
        }

        private fun configurarBotones(reserva: ReservaDto) {
            when (reserva.estatus) {
                1 -> { // Programada
                    layoutAcciones.visibility = View.VISIBLE

                    if (esCliente) {
                        // Cliente solo puede cancelar
                        btnNoShow.visibility = View.GONE
                        btnAccion.visibility = View.GONE
                        btnCancelar.visibility = View.VISIBLE
                    } else {
                        // Recepcionista puede todo
                        btnNoShow.visibility = View.VISIBLE
                        btnAccion.visibility = View.VISIBLE
                        btnCancelar.visibility = View.VISIBLE

                        btnAccion.text = "Iniciar"
                        btnAccion.isEnabled = reserva.puede_iniciar

                        btnAccion.setOnClickListener { onIniciar(reserva) }
                        btnNoShow.setOnClickListener { onNoShow(reserva) }
                    }

                    btnCancelar.setOnClickListener { onCancelar(reserva) }
                }
                2 -> { // En Curso
                    if (esCliente) {
                        // Cliente no puede hacer nada cuando está en curso
                        layoutAcciones.visibility = View.GONE
                    } else {
                        layoutAcciones.visibility = View.VISIBLE
                        btnNoShow.visibility = View.GONE
                        btnCancelar.visibility = View.VISIBLE
                        btnAccion.visibility = View.VISIBLE

                        btnAccion.text = "Completar"
                        btnAccion.isEnabled = true

                        btnAccion.setOnClickListener { onCompletar(reserva) }
                        btnCancelar.setOnClickListener { onCancelar(reserva) }
                    }
                }
                else -> {
                    layoutAcciones.visibility = View.GONE
                }
            }
        }

        private fun formatHora(fechaHora: String): String {
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val date = inputFormat.parse(fechaHora)
                date?.let { outputFormat.format(it) } ?: fechaHora.substring(11, 16)
            } catch (e: Exception) {
                try {
                    fechaHora.substring(11, 16)
                } catch (e2: Exception) {
                    fechaHora
                }
            }
        }
    }

    class ReservaDiffCallback : DiffUtil.ItemCallback<ReservaDto>() {
        override fun areItemsTheSame(oldItem: ReservaDto, newItem: ReservaDto): Boolean {
            return oldItem.id_reserva == newItem.id_reserva
        }

        override fun areContentsTheSame(oldItem: ReservaDto, newItem: ReservaDto): Boolean {
            return oldItem == newItem
        }
    }
}
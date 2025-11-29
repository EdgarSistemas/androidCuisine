package com.intellisoft.androidcuisine.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.data.remote.dto.MesaDto

class MesasSeleccionAdapter(
    private var mesaSeleccionadaId: Int?,
    private val onMesaClick: (MesaDto) -> Unit
) : ListAdapter<MesaDto, MesasSeleccionAdapter.MesaViewHolder>(MesaDiffCallback()) {

    fun setMesaSeleccionada(mesaId: Int?) {
        mesaSeleccionadaId = mesaId
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MesaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mesa_seleccion, parent, false)
        return MesaViewHolder(view)
    }

    override fun onBindViewHolder(holder: MesaViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MesaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardMesa: MaterialCardView = itemView.findViewById(R.id.cardMesa)
        private val tvMesa: TextView = itemView.findViewById(R.id.tvMesa)
        private val tvCapacidad: TextView = itemView.findViewById(R.id.tvCapacidad)
        private val chipEstatus: Chip = itemView.findViewById(R.id.chipEstatus)

        fun bind(mesa: MesaDto) {
            tvMesa.text = mesa.codigo_mesa.takeLast(6) // Mostrar últimos 6 caracteres
            tvCapacidad.text = "${mesa.capacidad} pers."
            chipEstatus.text = mesa.estatus_display

            val isSelected = mesaSeleccionadaId == mesa.id_mesa
            val isDisponible = mesa.estatus_actual == 1

            // Configurar apariencia según estado
            when {
                isSelected -> {
                    cardMesa.strokeColor = itemView.context.getColor(R.color.cuisine_green_dark)
                    cardMesa.strokeWidth = 4
                    cardMesa.setCardBackgroundColor(itemView.context.getColor(R.color.cuisine_green_light))
                }
                isDisponible -> {
                    cardMesa.strokeColor = itemView.context.getColor(android.R.color.transparent)
                    cardMesa.strokeWidth = 0
                    cardMesa.setCardBackgroundColor(itemView.context.getColor(R.color.cuisine_white_accent))
                }
                else -> {
                    cardMesa.strokeColor = itemView.context.getColor(android.R.color.transparent)
                    cardMesa.strokeWidth = 0
                    cardMesa.setCardBackgroundColor(itemView.context.getColor(R.color.cuisine_beige_background))
                    cardMesa.alpha = 0.6f
                }
            }

            // Chip color según estado
            when (mesa.estatus_actual) {
                1 -> { // Disponible
                    chipEstatus.setChipBackgroundColorResource(R.color.cuisine_green_light)
                    chipEstatus.setTextColor(itemView.context.getColor(R.color.cuisine_dark_text))
                }
                2 -> { // Ocupada
                    chipEstatus.setChipBackgroundColorResource(android.R.color.holo_red_light)
                    chipEstatus.setTextColor(itemView.context.getColor(R.color.white))
                }
                3 -> { // En Limpieza
                    chipEstatus.setChipBackgroundColorResource(android.R.color.holo_orange_light)
                    chipEstatus.setTextColor(itemView.context.getColor(R.color.white))
                }
                4 -> { // Fuera Servicio
                    chipEstatus.setChipBackgroundColorResource(android.R.color.darker_gray)
                    chipEstatus.setTextColor(itemView.context.getColor(R.color.white))
                }
            }

            cardMesa.setOnClickListener {
                onMesaClick(mesa)
            }
        }
    }

    class MesaDiffCallback : DiffUtil.ItemCallback<MesaDto>() {
        override fun areItemsTheSame(oldItem: MesaDto, newItem: MesaDto): Boolean {
            return oldItem.id_mesa == newItem.id_mesa
        }

        override fun areContentsTheSame(oldItem: MesaDto, newItem: MesaDto): Boolean {
            return oldItem == newItem
        }
    }
}
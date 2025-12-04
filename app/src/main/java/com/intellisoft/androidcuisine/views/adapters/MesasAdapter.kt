package com.intellisoft.androidcuisine.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.data.remote.dto.MesaClienteDto

class MesasAdapter(
    private val onItemClick: (MesaClienteDto) -> Unit
) : ListAdapter<MesaClienteDto, MesasAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mesa_cliente, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val card: MaterialCardView = itemView.findViewById(R.id.cardMesa)
        private val tvCodigo: TextView = itemView.findViewById(R.id.tvCodigo)
        private val tvCapacidad: TextView = itemView.findViewById(R.id.tvCapacidad)
        private val tvEstatus: TextView = itemView.findViewById(R.id.tvEstatus)

        fun bind(mesa: MesaClienteDto) {
            tvCodigo.text = mesa.codigo_mesa
            tvCapacidad.text = "${mesa.capacidad} personas"
            tvEstatus.text = mesa.estatus_display

            val context = itemView.context

            // Colores según estatus
            when (mesa.estatus_actual) {
                1 -> { // Disponible
                    card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.cuisine_green_light))
                    card.strokeColor = ContextCompat.getColor(context, R.color.cuisine_green_dark)
                    tvEstatus.setTextColor(ContextCompat.getColor(context, R.color.cuisine_green_dark))
                    card.alpha = 1f
                }
                2 -> { // Ocupada
                    card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.cuisine_red_light))
                    card.strokeColor = ContextCompat.getColor(context, R.color.cuisine_red)
                    tvEstatus.setTextColor(ContextCompat.getColor(context, R.color.cuisine_red))
                    card.alpha = 0.6f
                }
                3 -> { // En Limpieza
                    card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.cuisine_yellow_light))
                    card.strokeColor = ContextCompat.getColor(context, R.color.cuisine_yellow)
                    tvEstatus.setTextColor(ContextCompat.getColor(context, R.color.cuisine_yellow_dark))
                    card.alpha = 0.6f
                }
                else -> { // Fuera de Servicio
                    card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.cuisine_gray_light))
                    card.strokeColor = ContextCompat.getColor(context, R.color.cuisine_gray)
                    tvEstatus.setTextColor(ContextCompat.getColor(context, R.color.cuisine_gray))
                    card.alpha = 0.4f
                }
            }

            card.setOnClickListener {
                onItemClick(mesa)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<MesaClienteDto>() {
        override fun areItemsTheSame(oldItem: MesaClienteDto, newItem: MesaClienteDto): Boolean {
            return oldItem.id_mesa == newItem.id_mesa
        }

        override fun areContentsTheSame(oldItem: MesaClienteDto, newItem: MesaClienteDto): Boolean {
            return oldItem == newItem
        }
    }
}
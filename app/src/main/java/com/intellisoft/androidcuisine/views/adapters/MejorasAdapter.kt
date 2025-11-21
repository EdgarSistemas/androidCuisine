package com.intellisoft.androidcuisine.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.data.remote.dto.MejoraItemDto

class MejorasAdapter : RecyclerView.Adapter<MejorasAdapter.MejoraViewHolder>() {

    private var items: List<MejoraItemDto> = emptyList()

    fun submitList(newItems: List<MejoraItemDto>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MejoraViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mejora, parent, false)
        return MejoraViewHolder(view)
    }

    override fun onBindViewHolder(holder: MejoraViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    class MejoraViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNotas: TextView = itemView.findViewById(R.id.tvNotas)
        private val tvFecha: TextView = itemView.findViewById(R.id.tvFecha)
        private val chipEstatus: Chip = itemView.findViewById(R.id.chipEstatus)

        fun bind(mejora: MejoraItemDto) {
            tvNotas.text = mejora.notas

            // Formatear fecha simple (Tomar solo la parte YYYY-MM-DD)
            // Si la fecha es "2025-11-21T07:25:37...", tomamos los primeros 10 caracteres
            tvFecha.text = if (mejora.fechaCreacion.length >= 10) {
                mejora.fechaCreacion.substring(0, 10)
            } else {
                mejora.fechaCreacion
            }

            // Configurar el Chip según el estatus numérico
            when (mejora.estatus) {
                1 -> {
                    chipEstatus.text = "Registrada"
                    chipEstatus.setChipBackgroundColorResource(R.color.cuisine_beige_background)
                    chipEstatus.setTextColor(itemView.context.getColor(R.color.cuisine_dark_text))
                }
                2 -> {
                    chipEstatus.text = "En Proceso"
                    chipEstatus.setChipBackgroundColorResource(R.color.cuisine_green_light)
                    chipEstatus.setTextColor(itemView.context.getColor(R.color.cuisine_dark_text))
                }
                3 -> {
                    chipEstatus.text = "Completada"
                    chipEstatus.setChipBackgroundColorResource(R.color.cuisine_green_dark)
                    chipEstatus.setTextColor(itemView.context.getColor(R.color.white))
                }
                else -> {
                    chipEstatus.text = "Estado $mejora.estatus"
                    chipEstatus.setChipBackgroundColorResource(android.R.color.darker_gray)
                }
            }
        }
    }
}
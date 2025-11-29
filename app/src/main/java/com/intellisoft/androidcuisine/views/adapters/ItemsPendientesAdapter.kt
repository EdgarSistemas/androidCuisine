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
import com.intellisoft.androidcuisine.data.remote.dto.ItemPendienteDto

class ItemsPendientesAdapter(
    private val onMarcarListo: (ItemPendienteDto) -> Unit
) : ListAdapter<ItemPendienteDto, ItemsPendientesAdapter.ItemViewHolder>(ItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cocina_pendiente, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvFolio: TextView = itemView.findViewById(R.id.tvFolio)
        private val chipTipo: Chip = itemView.findViewById(R.id.chipTipo)
        private val tvTiempo: TextView = itemView.findViewById(R.id.tvTiempo)
        private val tvProducto: TextView = itemView.findViewById(R.id.tvProducto)
        private val tvCantidad: TextView = itemView.findViewById(R.id.tvCantidad)
        private val tvMesa: TextView = itemView.findViewById(R.id.tvMesa)
        private val cardNotas: MaterialCardView = itemView.findViewById(R.id.cardNotas)
        private val tvNotas: TextView = itemView.findViewById(R.id.tvNotas)
        private val btnListo: MaterialButton = itemView.findViewById(R.id.btnListo)

        fun bind(item: ItemPendienteDto) {
            tvFolio.text = item.folio_pedido
            tvTiempo.text = item.tiempo_en_cocina
            tvProducto.text = item.producto_nombre
            tvCantidad.text = "x${item.cantidad}"

            // Tipo de pedido
            chipTipo.text = item.tipo_pedido_display
            if (item.tipo_pedido == 2) {
                // Takeaway
                chipTipo.setChipBackgroundColorResource(android.R.color.holo_orange_light)
                chipTipo.setTextColor(itemView.context.getColor(R.color.white))
            } else {
                // Dine-in
                chipTipo.setChipBackgroundColorResource(R.color.cuisine_green_light)
                chipTipo.setTextColor(itemView.context.getColor(R.color.cuisine_dark_text))
            }

            // Mesa
            if (item.mesa_id != null) {
                tvMesa.text = "Mesa ${item.mesa_id}"
                tvMesa.visibility = View.VISIBLE
            } else {
                tvMesa.text = "Para llevar"
                tvMesa.visibility = View.VISIBLE
            }

            // Notas
            if (!item.notas.isNullOrEmpty()) {
                cardNotas.visibility = View.VISIBLE
                tvNotas.text = item.notas
            } else {
                cardNotas.visibility = View.GONE
            }

            // Color del tiempo según urgencia
            configurarTiempo(item.tiempo_en_cocina)

            btnListo.setOnClickListener {
                onMarcarListo(item)
            }
        }

        private fun configurarTiempo(tiempo: String) {
            // Extraer minutos del string para determinar urgencia
            val minutos = extraerMinutos(tiempo)

            when {
                minutos >= 15 -> {
                    tvTiempo.setTextColor(itemView.context.getColor(android.R.color.holo_red_dark))
                }
                minutos >= 10 -> {
                    tvTiempo.setTextColor(itemView.context.getColor(android.R.color.holo_orange_dark))
                }
                else -> {
                    tvTiempo.setTextColor(itemView.context.getColor(R.color.cuisine_green_dark))
                }
            }
        }

        private fun extraerMinutos(tiempo: String): Int {
            return try {
                when {
                    tiempo.contains("h") -> {
                        val horas = tiempo.substringBefore("h").trim().toIntOrNull() ?: 0
                        val minutos = tiempo.substringAfter("h").replace("m", "").trim().toIntOrNull() ?: 0
                        (horas * 60) + minutos
                    }
                    tiempo.contains("min") -> {
                        tiempo.replace("min", "").trim().toIntOrNull() ?: 0
                    }
                    else -> 0
                }
            } catch (e: Exception) {
                0
            }
        }
    }

    class ItemDiffCallback : DiffUtil.ItemCallback<ItemPendienteDto>() {
        override fun areItemsTheSame(oldItem: ItemPendienteDto, newItem: ItemPendienteDto): Boolean {
            return oldItem.id_pedido_item == newItem.id_pedido_item
        }

        override fun areContentsTheSame(oldItem: ItemPendienteDto, newItem: ItemPendienteDto): Boolean {
            return oldItem == newItem
        }
    }
}
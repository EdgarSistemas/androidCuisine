package com.intellisoft.androidcuisine.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.data.remote.dto.PedidoCocinaDto

class PedidosCocinaAdapter(
    private val onMarcarListo: (Int) -> Unit
) : ListAdapter<PedidoCocinaDto, PedidosCocinaAdapter.PedidoViewHolder>(PedidoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pedido_cocina, parent, false)
        return PedidoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PedidoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PedidoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvFolio: TextView = itemView.findViewById(R.id.tvFolio)
        private val chipTipo: Chip = itemView.findViewById(R.id.chipTipo)
        private val tvMesa: TextView = itemView.findViewById(R.id.tvMesa)
        private val tvProgreso: TextView = itemView.findViewById(R.id.tvProgreso)
        private val progressIndicator: LinearProgressIndicator = itemView.findViewById(R.id.progressIndicator)
        private val recyclerViewItems: RecyclerView = itemView.findViewById(R.id.recyclerViewItems)

        fun bind(pedido: PedidoCocinaDto) {
            tvFolio.text = pedido.folio

            // Tipo de pedido
            chipTipo.text = pedido.tipo_pedido_display
            if (pedido.tipo_pedido == 2) {
                chipTipo.setChipBackgroundColorResource(android.R.color.holo_orange_light)
                chipTipo.setTextColor(itemView.context.getColor(R.color.white))
            } else {
                chipTipo.setChipBackgroundColorResource(R.color.cuisine_green_light)
                chipTipo.setTextColor(itemView.context.getColor(R.color.cuisine_dark_text))
            }

            // Mesa
            tvMesa.text = if (pedido.mesa_id != null) "Mesa ${pedido.mesa_id}" else "Para llevar"

            // Progreso
            tvProgreso.text = "Progreso: ${pedido.progreso}"
            configurarProgreso(pedido.progreso)

            // Items
            val itemsAdapter = ItemsEnPedidoAdapter(onMarcarListo)
            recyclerViewItems.layoutManager = LinearLayoutManager(itemView.context)
            recyclerViewItems.adapter = itemsAdapter
            itemsAdapter.submitList(pedido.items_en_cocina)
        }

        private fun configurarProgreso(progreso: String) {
            try {
                val partes = progreso.split("/")
                if (partes.size == 2) {
                    val listos = partes[0].toIntOrNull() ?: 0
                    val total = partes[1].toIntOrNull() ?: 1
                    val porcentaje = if (total > 0) (listos * 100) / total else 0
                    progressIndicator.progress = porcentaje
                }
            } catch (e: Exception) {
                progressIndicator.progress = 0
            }
        }
    }

    class PedidoDiffCallback : DiffUtil.ItemCallback<PedidoCocinaDto>() {
        override fun areItemsTheSame(oldItem: PedidoCocinaDto, newItem: PedidoCocinaDto): Boolean {
            return oldItem.id_pedido == newItem.id_pedido
        }

        override fun areContentsTheSame(oldItem: PedidoCocinaDto, newItem: PedidoCocinaDto): Boolean {
            return oldItem == newItem
        }
    }
}
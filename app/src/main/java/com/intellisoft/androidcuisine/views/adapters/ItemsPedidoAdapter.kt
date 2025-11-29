package com.intellisoft.androidcuisine.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.data.remote.dto.ItemPedidoDetalleDto

class ItemsPedidoAdapter : ListAdapter<ItemPedidoDetalleDto, ItemsPedidoAdapter.ItemViewHolder>(ItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pedido_detalle, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvCantidad: TextView = itemView.findViewById(R.id.tvCantidad)
        private val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        private val tvNotas: TextView = itemView.findViewById(R.id.tvNotas)
        private val chipEstado: Chip = itemView.findViewById(R.id.chipEstadoItem)

        fun bind(item: ItemPedidoDetalleDto) {
            tvCantidad.text = "${item.cantidad}x"
            tvNombre.text = item.producto ?: item.combo ?: "Item"

            if (!item.notas.isNullOrEmpty()) {
                tvNotas.text = item.notas
                tvNotas.visibility = View.VISIBLE
            } else {
                tvNotas.visibility = View.GONE
            }

            chipEstado.text = item.estado_descripcion ?: getEstadoItemTexto(item.estado)
            configurarChipEstado(item.estado)
        }

        private fun configurarChipEstado(estado: Int) {
            when (estado) {
                0 -> {
                    chipEstado.setChipBackgroundColorResource(android.R.color.darker_gray)
                    chipEstado.setTextColor(itemView.context.getColor(R.color.white))
                }
                1 -> {
                    chipEstado.setChipBackgroundColorResource(android.R.color.holo_orange_light)
                    chipEstado.setTextColor(itemView.context.getColor(R.color.cuisine_dark_text))
                }
                2 -> {
                    chipEstado.setChipBackgroundColorResource(R.color.cuisine_green_dark)
                    chipEstado.setTextColor(itemView.context.getColor(R.color.white))
                }
                3 -> {
                    chipEstado.setChipBackgroundColorResource(R.color.cuisine_green_light)
                    chipEstado.setTextColor(itemView.context.getColor(R.color.cuisine_dark_text))
                }
                4 -> {
                    chipEstado.setChipBackgroundColorResource(android.R.color.holo_red_light)
                    chipEstado.setTextColor(itemView.context.getColor(R.color.white))
                }
            }
        }

        private fun getEstadoItemTexto(estado: Int): String {
            return when (estado) {
                0 -> "Pendiente"
                1 -> "En cocina"
                2 -> "Listo"
                3 -> "Entregado"
                4 -> "Cancelado"
                else -> "Desconocido"
            }
        }
    }

    class ItemDiffCallback : DiffUtil.ItemCallback<ItemPedidoDetalleDto>() {
        override fun areItemsTheSame(oldItem: ItemPedidoDetalleDto, newItem: ItemPedidoDetalleDto): Boolean {
            return oldItem.id_item == newItem.id_item
        }

        override fun areContentsTheSame(oldItem: ItemPedidoDetalleDto, newItem: ItemPedidoDetalleDto): Boolean {
            return oldItem == newItem
        }
    }
}
package com.intellisoft.androidcuisine.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.data.remote.dto.ItemEnCocinaDto

class ItemsEnPedidoAdapter(
    private val onMarcarListo: (Int) -> Unit
) : ListAdapter<ItemEnCocinaDto, ItemsEnPedidoAdapter.ItemViewHolder>(ItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pedido_cocina_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvCantidad: TextView = itemView.findViewById(R.id.tvCantidad)
        private val tvProducto: TextView = itemView.findViewById(R.id.tvProducto)
        private val tvNotas: TextView = itemView.findViewById(R.id.tvNotas)
        private val btnListo: MaterialButton = itemView.findViewById(R.id.btnListo)

        fun bind(item: ItemEnCocinaDto) {
            tvCantidad.text = "x${item.cantidad}"
            tvProducto.text = item.producto_nombre ?: "Producto"

            if (!item.notas.isNullOrEmpty()) {
                tvNotas.text = item.notas
                tvNotas.visibility = View.VISIBLE
            } else {
                tvNotas.visibility = View.GONE
            }

            btnListo.setOnClickListener {
                onMarcarListo(item.id_pedido_item)
            }
        }
    }

    class ItemDiffCallback : DiffUtil.ItemCallback<ItemEnCocinaDto>() {
        override fun areItemsTheSame(oldItem: ItemEnCocinaDto, newItem: ItemEnCocinaDto): Boolean {
            return oldItem.id_pedido_item == newItem.id_pedido_item
        }

        override fun areContentsTheSame(oldItem: ItemEnCocinaDto, newItem: ItemEnCocinaDto): Boolean {
            return oldItem == newItem
        }
    }
}
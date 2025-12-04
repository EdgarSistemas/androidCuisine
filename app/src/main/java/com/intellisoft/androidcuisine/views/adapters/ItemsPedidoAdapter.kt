package com.intellisoft.androidcuisine.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.data.remote.dto.ItemPedidoDto

class ItemsPedidoAdapter : ListAdapter<ItemPedidoDto, ItemsPedidoAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pedido_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvCantidad: TextView = itemView.findViewById(R.id.tvCantidad)
        private val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        private val tvPrecio: TextView = itemView.findViewById(R.id.tvPrecio)
        private val tvNotas: TextView = itemView.findViewById(R.id.tvNotas)

        fun bind(item: ItemPedidoDto) {
            tvCantidad.text = "${item.cantidad}x"
            tvNombre.text = item.producto_nombre ?: item.combo_nombre ?: "Producto"
            tvPrecio.text = "$${String.format("%.2f", (item.precio_unit ?: 0.0) * (item.cantidad ?: 1))}"

            if (item.notas.isNullOrEmpty()) {
                tvNotas.visibility = View.GONE
            } else {
                tvNotas.visibility = View.VISIBLE
                tvNotas.text = "📝 ${item.notas}"
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ItemPedidoDto>() {
        override fun areItemsTheSame(oldItem: ItemPedidoDto, newItem: ItemPedidoDto): Boolean {
            return oldItem.id_pedido_item == newItem.id_pedido_item
        }

        override fun areContentsTheSame(oldItem: ItemPedidoDto, newItem: ItemPedidoDto): Boolean {
            return oldItem == newItem
        }
    }
}
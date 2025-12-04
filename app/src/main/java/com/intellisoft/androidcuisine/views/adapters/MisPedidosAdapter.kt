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
import com.google.android.material.chip.Chip
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.data.remote.dto.PedidoClienteDto

class MisPedidosAdapter(
    private val onItemClick: (PedidoClienteDto) -> Unit
) : ListAdapter<PedidoClienteDto, MisPedidosAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pedido_cliente, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val card: MaterialCardView = itemView.findViewById(R.id.cardPedido)
        private val tvIdPedido: TextView = itemView.findViewById(R.id.tvIdPedido)
        private val tvFecha: TextView = itemView.findViewById(R.id.tvFecha)
        private val tvTipo: TextView = itemView.findViewById(R.id.tvTipo)
        private val chipEstatus: Chip = itemView.findViewById(R.id.chipEstatus)
        private val tvTotal: TextView = itemView.findViewById(R.id.tvTotal)

        fun bind(pedido: PedidoClienteDto) {
            val context = itemView.context

            tvIdPedido.text = "Pedido #${pedido.id_pedido}"
            tvFecha.text = "📅 ${formatearFecha(pedido.created_at ?: "")}"

            val tipoTexto = when (pedido.tipo_pedido) {
                1 -> "🍽️ Comer aquí"
                2 -> "🥡 Para llevar"
                else -> "Pedido"
            }
            tvTipo.text = tipoTexto

            chipEstatus.text = pedido.estado_display ?: "Pendiente"
            when (pedido.estado_pedido) {
                1 -> {
                    chipEstatus.setChipBackgroundColorResource(R.color.cuisine_yellow_light)
                    chipEstatus.setTextColor(ContextCompat.getColor(context, R.color.cuisine_yellow_dark))
                }
                2 -> {
                    chipEstatus.setChipBackgroundColorResource(R.color.cuisine_blue_light)
                    chipEstatus.setTextColor(ContextCompat.getColor(context, R.color.cuisine_blue))
                }
                3 -> {
                    chipEstatus.setChipBackgroundColorResource(R.color.cuisine_green_light)
                    chipEstatus.setTextColor(ContextCompat.getColor(context, R.color.cuisine_green_dark))
                }
                4 -> {
                    chipEstatus.setChipBackgroundColorResource(R.color.cuisine_gray_light)
                    chipEstatus.setTextColor(ContextCompat.getColor(context, R.color.cuisine_gray))
                }
                5 -> {
                    chipEstatus.setChipBackgroundColorResource(R.color.cuisine_red_light)
                    chipEstatus.setTextColor(ContextCompat.getColor(context, R.color.cuisine_red))
                }
            }

            tvTotal.text = "$${String.format("%.2f", pedido.total ?: 0.0)}"

            card.setOnClickListener {
                onItemClick(pedido)
            }
        }

        private fun formatearFecha(fecha: String): String {
            return try {
                val partes = fecha.split("T")
                if (partes.size >= 2) {
                    "${partes[0]} ${partes[1].substring(0, 5)}"
                } else {
                    fecha.take(16)
                }
            } catch (e: Exception) {
                fecha
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<PedidoClienteDto>() {
        override fun areItemsTheSame(oldItem: PedidoClienteDto, newItem: PedidoClienteDto): Boolean {
            return oldItem.id_pedido == newItem.id_pedido
        }

        override fun areContentsTheSame(oldItem: PedidoClienteDto, newItem: PedidoClienteDto): Boolean {
            return oldItem == newItem
        }
    }
}
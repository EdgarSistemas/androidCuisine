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
import com.intellisoft.androidcuisine.data.remote.dto.PedidoDetalleDto
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class MisPedidosClienteAdapter(
    private val onVerDetalle: (PedidoDetalleDto) -> Unit,
    private val onCancelar: (PedidoDetalleDto) -> Unit
) : ListAdapter<PedidoDetalleDto, MisPedidosClienteAdapter.PedidoViewHolder>(PedidoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pedido_cliente, parent, false)
        return PedidoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PedidoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PedidoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardPedido: MaterialCardView = itemView.findViewById(R.id.cardPedido)
        private val tvPedidoId: TextView = itemView.findViewById(R.id.tvPedidoId)
        private val tvFecha: TextView = itemView.findViewById(R.id.tvFecha)
        private val chipTipo: Chip = itemView.findViewById(R.id.chipTipo)
        private val chipEstado: Chip = itemView.findViewById(R.id.chipEstado)
        private val tvItems: TextView = itemView.findViewById(R.id.tvItems)
        private val tvTotal: TextView = itemView.findViewById(R.id.tvTotal)
        private val btnVerDetalle: MaterialButton = itemView.findViewById(R.id.btnVerDetalle)
        private val btnCancelar: MaterialButton = itemView.findViewById(R.id.btnCancelar)

        private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "MX"))

        fun bind(pedido: PedidoDetalleDto) {
            tvPedidoId.text = "Pedido #${pedido.id_pedido}"
            tvFecha.text = formatearFecha(pedido.created_at ?: "")

            chipTipo.text = pedido.tipo_descripcion ?: getTipoTexto(pedido.tipo_pedido)
            when (pedido.tipo_pedido) {
                1 -> {
                    chipTipo.setChipBackgroundColorResource(R.color.cuisine_green_light)
                    chipTipo.setTextColor(itemView.context.getColor(R.color.cuisine_dark_text))
                }
                2 -> {
                    chipTipo.setChipBackgroundColorResource(android.R.color.holo_orange_light)
                    chipTipo.setTextColor(itemView.context.getColor(R.color.cuisine_dark_text))
                }
            }

            chipEstado.text = pedido.estado_descripcion ?: getEstadoTexto(pedido.estado)
            configurarEstadoVisual(pedido.estado)

            val cantidadItems = pedido.items?.sumOf { it.cantidad } ?: 0
            tvItems.text = "$cantidadItems items"

            tvTotal.text = currencyFormat.format(pedido.total ?: 0.0)

            btnVerDetalle.setOnClickListener { onVerDetalle(pedido) }

            if (pedido.estado == 0) {
                btnCancelar.visibility = View.VISIBLE
                btnCancelar.setOnClickListener { onCancelar(pedido) }
            } else {
                btnCancelar.visibility = View.GONE
            }

            cardPedido.setOnClickListener { onVerDetalle(pedido) }
        }

        private fun configurarEstadoVisual(estado: Int) {
            when (estado) {
                0 -> {
                    chipEstado.setChipBackgroundColorResource(R.color.cuisine_green_dark)
                    chipEstado.setTextColor(itemView.context.getColor(R.color.white))
                    cardPedido.alpha = 1f
                }
                3 -> {
                    chipEstado.setChipBackgroundColorResource(android.R.color.holo_blue_light)
                    chipEstado.setTextColor(itemView.context.getColor(R.color.white))
                    cardPedido.alpha = 0.9f
                }
                4 -> {
                    chipEstado.setChipBackgroundColorResource(android.R.color.holo_red_light)
                    chipEstado.setTextColor(itemView.context.getColor(R.color.white))
                    cardPedido.alpha = 0.6f
                }
                5 -> {
                    chipEstado.setChipBackgroundColorResource(android.R.color.darker_gray)
                    chipEstado.setTextColor(itemView.context.getColor(R.color.white))
                    cardPedido.alpha = 0.8f
                }
                else -> {
                    chipEstado.setChipBackgroundColorResource(android.R.color.darker_gray)
                    chipEstado.setTextColor(itemView.context.getColor(R.color.white))
                }
            }
        }

        private fun getTipoTexto(tipo: Int): String {
            return when (tipo) {
                1 -> "En sitio"
                2 -> "Para llevar"
                else -> "Pedido"
            }
        }

        private fun getEstadoTexto(estado: Int): String {
            return when (estado) {
                0 -> "En preparación"
                3 -> "Completo"
                4 -> "Cancelado"
                5 -> "Pagado"
                else -> "Desconocido"
            }
        }

        private fun formatearFecha(fechaHora: String): String {
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                val date = inputFormat.parse(fechaHora)
                if (date != null) outputFormat.format(date) else fechaHora
            } catch (e: Exception) {
                try {
                    fechaHora.substring(0, 16).replace("T", " ")
                } catch (e2: Exception) {
                    fechaHora
                }
            }
        }
    }

    class PedidoDiffCallback : DiffUtil.ItemCallback<PedidoDetalleDto>() {
        override fun areItemsTheSame(oldItem: PedidoDetalleDto, newItem: PedidoDetalleDto): Boolean {
            return oldItem.id_pedido == newItem.id_pedido
        }

        override fun areContentsTheSame(oldItem: PedidoDetalleDto, newItem: PedidoDetalleDto): Boolean {
            return oldItem == newItem
        }
    }
}
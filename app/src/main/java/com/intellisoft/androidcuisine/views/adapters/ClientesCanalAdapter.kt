package com.intellisoft.androidcuisine.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.chip.Chip
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.data.remote.dto.ClienteCanalDto

class ClientesCanalAdapter(
    private val clientesSeleccionados: MutableSet<Int>
) : ListAdapter<ClienteCanalDto, ClientesCanalAdapter.ClienteViewHolder>(ClienteDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClienteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cliente_canal, parent, false)
        return ClienteViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClienteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ClienteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val checkboxCliente: MaterialCheckBox = itemView.findViewById(R.id.checkboxCliente)
        private val tvNombreCliente: TextView = itemView.findViewById(R.id.tvNombreCliente)
        private val tvPedidosMesa: TextView = itemView.findViewById(R.id.tvPedidosMesa)
        private val tvPedidosTakeaway: TextView = itemView.findViewById(R.id.tvPedidosTakeaway)
        private val tvPedidosDelivery: TextView = itemView.findViewById(R.id.tvPedidosDelivery)
        private val tvTotalReservas: TextView = itemView.findViewById(R.id.tvTotalReservas)
        private val chipCanal: Chip = itemView.findViewById(R.id.chipCanal)

        fun bind(cliente: ClienteCanalDto) {
            tvNombreCliente.text = cliente.nombre_completo
            tvPedidosMesa.text = cliente.pedidos_mesa.toString()
            tvPedidosTakeaway.text = cliente.pedidos_takeaway.toString()
            tvPedidosDelivery.text = cliente.pedidos_delivery.toString()
            tvTotalReservas.text = cliente.total_reservas.toString()

            configurarCanal(cliente.canal_preferido)

            checkboxCliente.setOnCheckedChangeListener(null)
            checkboxCliente.isChecked = clientesSeleccionados.contains(cliente.id_usuario)
            checkboxCliente.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    clientesSeleccionados.add(cliente.id_usuario)
                } else {
                    clientesSeleccionados.remove(cliente.id_usuario)
                }
            }
        }

        private fun configurarCanal(canal: String) {
            val canalTexto = when (canal) {
                "MESA_PREFERIDO" -> "Mesa"
                "TAKEAWAY_PREFERIDO" -> "Takeaway"
                "DELIVERY_PREFERIDO" -> "Delivery"
                "RESERVA_PREFERIDO" -> "Reserva"
                "MIXTO" -> "Mixto"
                else -> canal
            }
            chipCanal.text = canalTexto

            when (canal) {
                "MESA_PREFERIDO" -> {
                    chipCanal.setChipBackgroundColorResource(R.color.cuisine_green_dark)
                    chipCanal.setTextColor(itemView.context.getColor(R.color.white))
                }
                "TAKEAWAY_PREFERIDO" -> {
                    chipCanal.setChipBackgroundColorResource(android.R.color.holo_blue_light)
                    chipCanal.setTextColor(itemView.context.getColor(R.color.white))
                }
                "DELIVERY_PREFERIDO" -> {
                    chipCanal.setChipBackgroundColorResource(android.R.color.holo_orange_light)
                    chipCanal.setTextColor(itemView.context.getColor(R.color.white))
                }
                "RESERVA_PREFERIDO" -> {
                    chipCanal.setChipBackgroundColorResource(android.R.color.holo_purple)
                    chipCanal.setTextColor(itemView.context.getColor(R.color.white))
                }
                else -> {
                    chipCanal.setChipBackgroundColorResource(R.color.cuisine_beige_background)
                    chipCanal.setTextColor(itemView.context.getColor(R.color.cuisine_dark_text))
                }
            }
        }
    }

    class ClienteDiffCallback : DiffUtil.ItemCallback<ClienteCanalDto>() {
        override fun areItemsTheSame(oldItem: ClienteCanalDto, newItem: ClienteCanalDto): Boolean {
            return oldItem.id_usuario == newItem.id_usuario
        }

        override fun areContentsTheSame(oldItem: ClienteCanalDto, newItem: ClienteCanalDto): Boolean {
            return oldItem == newItem
        }
    }
}
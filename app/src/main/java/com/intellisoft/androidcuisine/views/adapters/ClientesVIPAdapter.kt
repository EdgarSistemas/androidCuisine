package com.intellisoft.androidcuisine.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.checkbox.MaterialCheckBox
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.data.remote.dto.ClienteVIPDto
import java.text.NumberFormat
import java.util.*

class ClientesVIPAdapter(
    private val clientesSeleccionados: MutableSet<Int>
) : ListAdapter<ClienteVIPDto, ClientesVIPAdapter.ClienteViewHolder>(ClienteDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClienteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cliente_vip, parent, false)
        return ClienteViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClienteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ClienteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val checkboxCliente: MaterialCheckBox = itemView.findViewById(R.id.checkboxCliente)
        private val tvNombreCliente: TextView = itemView.findViewById(R.id.tvNombreCliente)
        private val tvEmail: TextView = itemView.findViewById(R.id.tvEmail)
        private val tvGastoTotal: TextView = itemView.findViewById(R.id.tvGastoTotal)
        private val tvTotalPedidos: TextView = itemView.findViewById(R.id.tvTotalPedidos)
        private val tvTicketPromedio: TextView = itemView.findViewById(R.id.tvTicketPromedio)

        fun bind(cliente: ClienteVIPDto) {
            tvNombreCliente.text = cliente.nombre_completo
            tvEmail.text = cliente.email
            tvGastoTotal.text = formatCurrency(cliente.gasto_total)
            tvTotalPedidos.text = cliente.total_pedidos.toString()
            tvTicketPromedio.text = formatCurrency(cliente.ticket_promedio)

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

        private fun formatCurrency(amount: Double): String {
            val format = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
            return format.format(amount)
        }
    }

    class ClienteDiffCallback : DiffUtil.ItemCallback<ClienteVIPDto>() {
        override fun areItemsTheSame(oldItem: ClienteVIPDto, newItem: ClienteVIPDto): Boolean {
            return oldItem.id_usuario == newItem.id_usuario
        }

        override fun areContentsTheSame(oldItem: ClienteVIPDto, newItem: ClienteVIPDto): Boolean {
            return oldItem == newItem
        }
    }
}
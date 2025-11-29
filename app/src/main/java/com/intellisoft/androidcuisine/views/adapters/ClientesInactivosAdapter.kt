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
import com.intellisoft.androidcuisine.data.remote.dto.ClienteInactivoDto
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class ClientesInactivosAdapter(
    private val clientesSeleccionados: MutableSet<Int>
) : ListAdapter<ClienteInactivoDto, ClientesInactivosAdapter.ClienteViewHolder>(ClienteDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClienteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cliente_inactivo, parent, false)
        return ClienteViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClienteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ClienteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val checkboxCliente: MaterialCheckBox = itemView.findViewById(R.id.checkboxCliente)
        private val tvNombreCliente: TextView = itemView.findViewById(R.id.tvNombreCliente)
        private val tvUltimoPedido: TextView = itemView.findViewById(R.id.tvUltimoPedido)
        private val tvHistoricoPedidos: TextView = itemView.findViewById(R.id.tvHistoricoPedidos)
        private val tvGastoHistorico: TextView = itemView.findViewById(R.id.tvGastoHistorico)
        private val chipRiesgo: Chip = itemView.findViewById(R.id.chipRiesgo)

        fun bind(cliente: ClienteInactivoDto) {
            tvNombreCliente.text = cliente.nombre_completo
            tvUltimoPedido.text = "Último pedido: ${formatFecha(cliente.ultimo_pedido)} (${cliente.dias_sin_comprar} días)"
            tvHistoricoPedidos.text = cliente.historico_pedidos.toString()
            tvGastoHistorico.text = formatCurrency(cliente.gasto_historico)

            configurarRiesgo(cliente.nivel_riesgo)

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

        private fun configurarRiesgo(nivel: String) {
            chipRiesgo.text = nivel
            when (nivel) {
                "EN_RIESGO" -> {
                    chipRiesgo.setChipBackgroundColorResource(R.color.cuisine_green_light)
                    chipRiesgo.setTextColor(itemView.context.getColor(R.color.cuisine_dark_text))
                }
                "INACTIVO" -> {
                    chipRiesgo.setChipBackgroundColorResource(android.R.color.holo_orange_light)
                    chipRiesgo.setTextColor(itemView.context.getColor(R.color.white))
                }
                "PERDIDO" -> {
                    chipRiesgo.setChipBackgroundColorResource(android.R.color.holo_red_dark)
                    chipRiesgo.setTextColor(itemView.context.getColor(R.color.white))
                }
            }
        }

        private fun formatFecha(fecha: String): String {
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val date = inputFormat.parse(fecha)
                date?.let { outputFormat.format(it) } ?: fecha
            } catch (e: Exception) {
                fecha
            }
        }

        private fun formatCurrency(amount: Double): String {
            val format = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
            return format.format(amount)
        }
    }

    class ClienteDiffCallback : DiffUtil.ItemCallback<ClienteInactivoDto>() {
        override fun areItemsTheSame(oldItem: ClienteInactivoDto, newItem: ClienteInactivoDto): Boolean {
            return oldItem.id_usuario == newItem.id_usuario
        }

        override fun areContentsTheSame(oldItem: ClienteInactivoDto, newItem: ClienteInactivoDto): Boolean {
            return oldItem == newItem
        }
    }
}
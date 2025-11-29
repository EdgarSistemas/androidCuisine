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
import com.intellisoft.androidcuisine.data.remote.dto.ClienteNuevoDto
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class ClientesNuevosAdapter(
    private val clientesSeleccionados: MutableSet<Int>
) : ListAdapter<ClienteNuevoDto, ClientesNuevosAdapter.ClienteViewHolder>(ClienteDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClienteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cliente_nuevo, parent, false)
        return ClienteViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClienteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ClienteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val checkboxCliente: MaterialCheckBox = itemView.findViewById(R.id.checkboxCliente)
        private val tvNombreCliente: TextView = itemView.findViewById(R.id.tvNombreCliente)
        private val tvFechaRegistro: TextView = itemView.findViewById(R.id.tvFechaRegistro)
        private val tvTotalPedidos: TextView = itemView.findViewById(R.id.tvTotalPedidos)
        private val tvGastoTotal: TextView = itemView.findViewById(R.id.tvGastoTotal)
        private val chipEtapa: Chip = itemView.findViewById(R.id.chipEtapa)

        fun bind(cliente: ClienteNuevoDto) {
            tvNombreCliente.text = cliente.nombre_completo
            tvFechaRegistro.text = "Registro: ${formatFecha(cliente.fecha_registro)} (${cliente.dias_desde_registro} días)"
            tvTotalPedidos.text = cliente.total_pedidos.toString()
            tvGastoTotal.text = formatCurrency(cliente.gasto_total)

            configurarEtapa(cliente.etapa)

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

        private fun configurarEtapa(etapa: String) {
            val etapaTexto = when (etapa) {
                "SIN_PEDIDOS" -> "Sin pedidos"
                "PRIMERA_COMPRA" -> "1ª compra"
                "EN_ADOPCION" -> "En adopción"
                else -> etapa
            }
            chipEtapa.text = etapaTexto

            when (etapa) {
                "SIN_PEDIDOS" -> {
                    chipEtapa.setChipBackgroundColorResource(R.color.cuisine_beige_background)
                    chipEtapa.setTextColor(itemView.context.getColor(R.color.cuisine_dark_text))
                }
                "PRIMERA_COMPRA" -> {
                    chipEtapa.setChipBackgroundColorResource(R.color.cuisine_green_light)
                    chipEtapa.setTextColor(itemView.context.getColor(R.color.cuisine_dark_text))
                }
                "EN_ADOPCION" -> {
                    chipEtapa.setChipBackgroundColorResource(R.color.cuisine_green_dark)
                    chipEtapa.setTextColor(itemView.context.getColor(R.color.white))
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

    class ClienteDiffCallback : DiffUtil.ItemCallback<ClienteNuevoDto>() {
        override fun areItemsTheSame(oldItem: ClienteNuevoDto, newItem: ClienteNuevoDto): Boolean {
            return oldItem.id_usuario == newItem.id_usuario
        }

        override fun areContentsTheSame(oldItem: ClienteNuevoDto, newItem: ClienteNuevoDto): Boolean {
            return oldItem == newItem
        }
    }
}
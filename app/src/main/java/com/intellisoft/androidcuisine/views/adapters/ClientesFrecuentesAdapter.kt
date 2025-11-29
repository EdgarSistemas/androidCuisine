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
import com.intellisoft.androidcuisine.data.remote.dto.ClienteFrecuenteDto
import java.text.SimpleDateFormat
import java.util.*

class ClientesFrecuentesAdapter(
    private val clientesSeleccionados: MutableSet<Int>
) : ListAdapter<ClienteFrecuenteDto, ClientesFrecuentesAdapter.ClienteViewHolder>(ClienteDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClienteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cliente_frecuente, parent, false)
        return ClienteViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClienteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ClienteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val checkboxCliente: MaterialCheckBox = itemView.findViewById(R.id.checkboxCliente)
        private val tvNombreCliente: TextView = itemView.findViewById(R.id.tvNombreCliente)
        private val tvTotalPedidos: TextView = itemView.findViewById(R.id.tvTotalPedidos)
        private val tvTotalReservas: TextView = itemView.findViewById(R.id.tvTotalReservas)
        private val tvTotalInteracciones: TextView = itemView.findViewById(R.id.tvTotalInteracciones)
        private val tvFechas: TextView = itemView.findViewById(R.id.tvFechas)

        fun bind(cliente: ClienteFrecuenteDto) {
            tvNombreCliente.text = cliente.nombre_completo
            tvTotalPedidos.text = cliente.total_pedidos.toString()
            tvTotalReservas.text = cliente.total_reservas.toString()
            tvTotalInteracciones.text = cliente.total_interacciones.toString()
            tvFechas.text = "Primera: ${formatFecha(cliente.primera_visita)} | Última: ${formatFecha(cliente.ultima_visita)}"

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
    }

    class ClienteDiffCallback : DiffUtil.ItemCallback<ClienteFrecuenteDto>() {
        override fun areItemsTheSame(oldItem: ClienteFrecuenteDto, newItem: ClienteFrecuenteDto): Boolean {
            return oldItem.id_usuario == newItem.id_usuario
        }

        override fun areContentsTheSame(oldItem: ClienteFrecuenteDto, newItem: ClienteFrecuenteDto): Boolean {
            return oldItem == newItem
        }
    }
}
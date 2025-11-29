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
import com.intellisoft.androidcuisine.data.remote.dto.CompraData
import java.text.SimpleDateFormat
import java.util.*

class ComprasAdapter(
    private val onCompraClick: (CompraData) -> Unit
) : ListAdapter<CompraData, ComprasAdapter.CompraViewHolder>(CompraDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompraViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_compra, parent, false)
        return CompraViewHolder(view)
    }

    override fun onBindViewHolder(holder: CompraViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CompraViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvFolio: TextView = itemView.findViewById(R.id.tvFolio)
        private val tvFecha: TextView = itemView.findViewById(R.id.tvFecha)
        private val chipEstado: Chip = itemView.findViewById(R.id.chipEstado)
        private val tvProveedor: TextView = itemView.findViewById(R.id.tvProveedor)
        private val tvSucursal: TextView = itemView.findViewById(R.id.tvSucursal)

        fun bind(compra: CompraData) {
            tvFolio.text = compra.folio
            tvFecha.text = formatFecha(compra.fecha_compra)

            // TODO: Aquí deberías obtener el nombre del proveedor y sucursal
            tvProveedor.text = "Proveedor ID: ${compra.proveedor_id}"
            tvSucursal.text = "Sucursal ID: ${compra.sucursal_id}"

            configurarEstado(compra.estatus)

            itemView.setOnClickListener {
                onCompraClick(compra)
            }
        }

        private fun configurarEstado(estatus: Int) {
            when (estatus) {
                1 -> {
                    chipEstado.text = "Pendiente"
                    chipEstado.setChipBackgroundColorResource(R.color.cuisine_green_light)
                    chipEstado.setTextColor(itemView.context.getColor(R.color.cuisine_dark_text))
                }
                2 -> {
                    chipEstado.text = "Recibida"
                    chipEstado.setChipBackgroundColorResource(R.color.cuisine_green_dark)
                    chipEstado.setTextColor(itemView.context.getColor(R.color.white))
                }
                3 -> {
                    chipEstado.text = "Cancelada"
                    chipEstado.setChipBackgroundColorResource(R.color.cuisine_beige_background)
                    chipEstado.setTextColor(itemView.context.getColor(R.color.cuisine_dark_text))
                }
            }
        }

        private fun formatFecha(fecha: String): String {
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                val date = inputFormat.parse(fecha)
                date?.let { outputFormat.format(it) } ?: fecha
            } catch (e: Exception) {
                fecha
            }
        }
    }

    class CompraDiffCallback : DiffUtil.ItemCallback<CompraData>() {
        override fun areItemsTheSame(oldItem: CompraData, newItem: CompraData): Boolean {
            return oldItem.id_compra == newItem.id_compra
        }

        override fun areContentsTheSame(oldItem: CompraData, newItem: CompraData): Boolean {
            return oldItem == newItem
        }
    }
}
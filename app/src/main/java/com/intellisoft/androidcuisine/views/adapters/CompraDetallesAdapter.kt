package com.intellisoft.androidcuisine.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.data.remote.dto.CompraDetalleData
import java.text.NumberFormat
import java.util.*

class CompraDetallesAdapter : ListAdapter<CompraDetalleData, CompraDetallesAdapter.DetalleViewHolder>(DetalleDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetalleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_compra_detalle, parent, false)
        return DetalleViewHolder(view)
    }

    override fun onBindViewHolder(holder: DetalleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DetalleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNombreInsumo: TextView = itemView.findViewById(R.id.tvNombreInsumo)
        private val tvSubtotal: TextView = itemView.findViewById(R.id.tvSubtotal)
        private val tvDetalles: TextView = itemView.findViewById(R.id.tvDetalles)

        fun bind(detalle: CompraDetalleData) {
            tvNombreInsumo.text = detalle.insumo?.nombre ?: "Insumo ID: ${detalle.insumo_id}"
            tvSubtotal.text = formatCurrency(detalle.subtotal ?: 0.0)

            val presentacion = detalle.presentacion ?: ""
            val detallesText = "${detalle.cant_presentacion} $presentacion × ${formatCurrency(detalle.costo_unit_present)}"
            tvDetalles.text = detallesText
        }

        private fun formatCurrency(amount: Double): String {
            val format = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
            return format.format(amount)
        }
    }

    class DetalleDiffCallback : DiffUtil.ItemCallback<CompraDetalleData>() {
        override fun areItemsTheSame(oldItem: CompraDetalleData, newItem: CompraDetalleData): Boolean {
            return oldItem.id_compra_detalle == newItem.id_compra_detalle
        }

        override fun areContentsTheSame(oldItem: CompraDetalleData, newItem: CompraDetalleData): Boolean {
            return oldItem == newItem
        }
    }
}
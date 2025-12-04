package com.intellisoft.androidcuisine.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.data.remote.dto.SucursalActivaDto

class SucursalesTakeawayAdapter(
    private val onItemClick: (SucursalActivaDto) -> Unit
) : ListAdapter<SucursalActivaDto, SucursalesTakeawayAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sucursal_takeaway, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val card: MaterialCardView = itemView.findViewById(R.id.cardSucursal)
        private val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        private val tvDireccion: TextView = itemView.findViewById(R.id.tvDireccion)
        private val tvTelefono: TextView = itemView.findViewById(R.id.tvTelefono)

        fun bind(sucursal: SucursalActivaDto) {
            tvNombre.text = sucursal.nombre
            tvDireccion.text = "📍 ${sucursal.direccion}"

            if (sucursal.telefono.isNullOrEmpty()) {
                tvTelefono.visibility = View.GONE
            } else {
                tvTelefono.visibility = View.VISIBLE
                tvTelefono.text = "📞 ${sucursal.telefono}"
            }

            card.setOnClickListener {
                onItemClick(sucursal)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<SucursalActivaDto>() {
        override fun areItemsTheSame(oldItem: SucursalActivaDto, newItem: SucursalActivaDto): Boolean {
            return oldItem.id_sucursal == newItem.id_sucursal
        }

        override fun areContentsTheSame(oldItem: SucursalActivaDto, newItem: SucursalActivaDto): Boolean {
            return oldItem == newItem
        }
    }
}
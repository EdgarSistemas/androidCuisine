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

class SucursalesSeleccionAdapter(
    private val onSucursalClick: (SucursalActivaDto) -> Unit
) : ListAdapter<SucursalActivaDto, SucursalesSeleccionAdapter.SucursalViewHolder>(SucursalDiffCallback()) {

    private var sucursalSeleccionadaId: Int? = null

    fun setSeleccionada(sucursalId: Int?) {
        val oldId = sucursalSeleccionadaId
        sucursalSeleccionadaId = sucursalId

        currentList.forEachIndexed { index, sucursal ->
            if (sucursal.id_sucursal == oldId || sucursal.id_sucursal == sucursalId) {
                notifyItemChanged(index)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SucursalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sucursal_seleccion, parent, false)
        return SucursalViewHolder(view)
    }

    override fun onBindViewHolder(holder: SucursalViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SucursalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardSucursal: MaterialCardView = itemView.findViewById(R.id.cardSucursal)
        private val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        private val tvDireccion: TextView = itemView.findViewById(R.id.tvDireccion)

        fun bind(sucursal: SucursalActivaDto) {
            tvNombre.text = sucursal.nombre
            tvDireccion.text = sucursal.direccion ?: sucursal.telefono ?: ""

            val isSeleccionada = sucursal.id_sucursal == sucursalSeleccionadaId

            if (isSeleccionada) {
                cardSucursal.strokeWidth = 3
                cardSucursal.strokeColor = itemView.context.getColor(R.color.cuisine_green_dark)
                cardSucursal.setCardBackgroundColor(itemView.context.getColor(R.color.cuisine_green_light))
            } else {
                cardSucursal.strokeWidth = 1
                cardSucursal.strokeColor = itemView.context.getColor(R.color.cuisine_green_light)
                cardSucursal.setCardBackgroundColor(itemView.context.getColor(R.color.cuisine_white_accent))
            }

            cardSucursal.setOnClickListener {
                onSucursalClick(sucursal)
            }
        }
    }

    class SucursalDiffCallback : DiffUtil.ItemCallback<SucursalActivaDto>() {
        override fun areItemsTheSame(oldItem: SucursalActivaDto, newItem: SucursalActivaDto): Boolean {
            return oldItem.id_sucursal == newItem.id_sucursal
        }

        override fun areContentsTheSame(oldItem: SucursalActivaDto, newItem: SucursalActivaDto): Boolean {
            return oldItem == newItem
        }
    }
}
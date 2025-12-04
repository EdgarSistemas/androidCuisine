package com.intellisoft.androidcuisine.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.data.remote.dto.AreaClienteDto

class AreasAdapter(
    private val onItemClick: (AreaClienteDto) -> Unit
) : ListAdapter<AreaClienteDto, AreasAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_area_cliente, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val card: MaterialCardView = itemView.findViewById(R.id.cardArea)
        private val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        private val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcion)

        fun bind(area: AreaClienteDto) {
            tvNombre.text = area.nombre

            if (area.descripcion.isNullOrEmpty()) {
                tvDescripcion.visibility = View.GONE
            } else {
                tvDescripcion.visibility = View.VISIBLE
                tvDescripcion.text = area.descripcion
            }

            card.setOnClickListener {
                onItemClick(area)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<AreaClienteDto>() {
        override fun areItemsTheSame(oldItem: AreaClienteDto, newItem: AreaClienteDto): Boolean {
            return oldItem.id_area == newItem.id_area
        }

        override fun areContentsTheSame(oldItem: AreaClienteDto, newItem: AreaClienteDto): Boolean {
            return oldItem == newItem
        }
    }
}
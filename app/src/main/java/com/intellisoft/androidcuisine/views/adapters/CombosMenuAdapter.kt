package com.intellisoft.androidcuisine.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.data.remote.dto.ComboDto

class CombosMenuAdapter(
    private val onAgregarClick: (ComboDto) -> Unit
) : ListAdapter<ComboDto, CombosMenuAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_combo_menu, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        private val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcion)
        private val tvPrecio: TextView = itemView.findViewById(R.id.tvPrecio)
        private val tvAhorro: TextView = itemView.findViewById(R.id.tvAhorro)
        private val btnAgregar: MaterialButton = itemView.findViewById(R.id.btnAgregar)

        fun bind(combo: ComboDto) {
            tvNombre.text = combo.nombre
            tvPrecio.text = "$${String.format("%.2f", combo.precio)}"

            if (combo.descripcion.isNullOrEmpty()) {
                tvDescripcion.visibility = View.GONE
            } else {
                tvDescripcion.visibility = View.VISIBLE
                tvDescripcion.text = combo.descripcion
            }

            // Mostrar ahorro si hay precio original
            combo.precio?.let { precioOriginal ->
                if (precioOriginal > combo.precio) {
                    val ahorro = precioOriginal - combo.precio
                    tvAhorro.visibility = View.VISIBLE
                    tvAhorro.text = "Ahorras $${String.format("%.2f", ahorro)}"
                } else {
                    tvAhorro.visibility = View.GONE
                }
            } ?: run {
                tvAhorro.visibility = View.GONE
            }

            btnAgregar.setOnClickListener {
                onAgregarClick(combo)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ComboDto>() {
        override fun areItemsTheSame(oldItem: ComboDto, newItem: ComboDto): Boolean {
            return oldItem.id_combo == newItem.id_combo
        }

        override fun areContentsTheSame(oldItem: ComboDto, newItem: ComboDto): Boolean {
            return oldItem == newItem
        }
    }
}
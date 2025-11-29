package com.intellisoft.androidcuisine.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.data.remote.dto.ComboDto
import java.text.NumberFormat
import java.util.*

class CombosMenuAdapter(
    private val onAgregar: (ComboDto) -> Unit
) : ListAdapter<ComboDto, CombosMenuAdapter.ComboViewHolder>(ComboDiffCallback()) {

    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "MX"))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComboViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_combo_menu, parent, false)
        return ComboViewHolder(view)
    }

    override fun onBindViewHolder(holder: ComboViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ComboViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardCombo: MaterialCardView = itemView.findViewById(R.id.cardCombo)
        private val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        private val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcion)
        private val tvContenido: TextView = itemView.findViewById(R.id.tvContenido)
        private val tvPrecio: TextView = itemView.findViewById(R.id.tvPrecio)
        private val btnAgregar: MaterialButton = itemView.findViewById(R.id.btnAgregar)

        fun bind(combo: ComboDto) {
            tvNombre.text = combo.nombre
            tvPrecio.text = currencyFormat.format(combo.precio)

            if (!combo.descripcion.isNullOrEmpty()) {
                tvDescripcion.text = combo.descripcion
                tvDescripcion.visibility = View.VISIBLE
            } else {
                tvDescripcion.visibility = View.GONE
            }

            // Mostrar contenido del combo
            val contenido = combo.productos?.joinToString(" • ") { "${it.cantidad}x ${it.nombre}" }
            if (!contenido.isNullOrEmpty()) {
                tvContenido.text = contenido
                tvContenido.visibility = View.VISIBLE
            } else {
                tvContenido.visibility = View.GONE
            }

            btnAgregar.setOnClickListener { onAgregar(combo) }
            cardCombo.setOnClickListener { onAgregar(combo) }
        }
    }

    class ComboDiffCallback : DiffUtil.ItemCallback<ComboDto>() {
        override fun areItemsTheSame(oldItem: ComboDto, newItem: ComboDto): Boolean {
            return oldItem.id_combo == newItem.id_combo
        }

        override fun areContentsTheSame(oldItem: ComboDto, newItem: ComboDto): Boolean {
            return oldItem == newItem
        }
    }
}
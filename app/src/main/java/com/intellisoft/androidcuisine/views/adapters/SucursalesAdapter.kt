package com.intellisoft.androidcuisine.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.data.remote.dto.SucursalDto
import java.text.SimpleDateFormat
import java.util.*

class SucursalesAdapter(
    private val onEditClick: (SucursalDto) -> Unit,
    private val onDeleteClick: (SucursalDto) -> Unit
) : ListAdapter<SucursalDto, SucursalesAdapter.SucursalViewHolder>(SucursalDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SucursalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sucursal, parent, false)
        return SucursalViewHolder(view)
    }

    override fun onBindViewHolder(holder: SucursalViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SucursalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        private val tvCodigo: TextView = itemView.findViewById(R.id.tvCodigo)
        private val tvDireccion: TextView = itemView.findViewById(R.id.tvDireccion)
        private val tvTelefono: TextView = itemView.findViewById(R.id.tvTelefono)
        private val tvFechas: TextView = itemView.findViewById(R.id.tvFechas)
        private val chipStatus: Chip = itemView.findViewById(R.id.chipStatus)
        private val btnEditar: MaterialButton = itemView.findViewById(R.id.btnEditar)
        private val btnEliminar: MaterialButton = itemView.findViewById(R.id.btnEliminar)

        fun bind(sucursal: SucursalDto) {
            tvNombre.text = sucursal.nombre
            tvCodigo.text = sucursal.codigo_sucursal
            tvDireccion.text = sucursal.direccion
            tvTelefono.text = sucursal.telefono

            // Configurar chip de estado
            if (sucursal.es_activa) {
                chipStatus.text = "Activa"
                chipStatus.setChipBackgroundColorResource(R.color.cuisine_green_dark)
                chipStatus.setTextColor(itemView.context.getColor(R.color.white))
            } else {
                chipStatus.text = "Inactiva"
                chipStatus.setChipBackgroundColorResource(R.color.cuisine_beige_background)
                chipStatus.setTextColor(itemView.context.getColor(R.color.cuisine_dark_text))
            }

            // Formatear fechas
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            try {
                val createdDate = inputFormat.parse(sucursal.created_at)
                val updatedDateStr = sucursal.updated_at.replace("T", " ").split(".")[0]
                val updatedDate = inputFormat.parse(updatedDateStr)

                val createdFormatted = createdDate?.let { outputFormat.format(it) } ?: sucursal.created_at
                val updatedFormatted = updatedDate?.let { outputFormat.format(it) } ?: sucursal.updated_at

                tvFechas.text = "Creado: $createdFormatted | Actualizado: $updatedFormatted"
            } catch (e: Exception) {
                tvFechas.text = "Creado: ${sucursal.created_at.take(10)}"
            }

            btnEditar.setOnClickListener { onEditClick(sucursal) }
            btnEliminar.setOnClickListener { onDeleteClick(sucursal) }
        }
    }

    class SucursalDiffCallback : DiffUtil.ItemCallback<SucursalDto>() {
        override fun areItemsTheSame(oldItem: SucursalDto, newItem: SucursalDto): Boolean {
            return oldItem.id_sucursal == newItem.id_sucursal
        }

        override fun areContentsTheSame(oldItem: SucursalDto, newItem: SucursalDto): Boolean {
            return oldItem == newItem
        }
    }
}
package com.intellisoft.androidcuisine.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.data.remote.dto.Sucursal
import java.text.SimpleDateFormat
import java.util.*

class SucursalesAdapter(
    private val onEditClick: (Sucursal) -> Unit,
    private val onDeleteClick: (Sucursal) -> Unit
) : ListAdapter<Sucursal, SucursalesAdapter.SucursalViewHolder>(SucursalDiffCallback()) {

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
        private val ivStatus: ImageView = itemView.findViewById(R.id.ivStatus)
        private val btnEditar: Button = itemView.findViewById(R.id.btnEditar)
        private val btnEliminar: Button = itemView.findViewById(R.id.btnEliminar)

        fun bind(sucursal: Sucursal) {
            tvNombre.text = sucursal.nombre
            tvCodigo.text = sucursal.codigo_sucursal
            tvDireccion.text = sucursal.direccion
            tvTelefono.text = sucursal.telefono

            // Formatear fechas
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            try {
                val createdDate = inputFormat.parse(sucursal.created_at)
                val updatedDate = inputFormat.parse(sucursal.updated_at.split("T")[0] + " " +
                    sucursal.updated_at.split("T")[1].split(".")[0])

                val createdFormatted = createdDate?.let { outputFormat.format(it) } ?: sucursal.created_at
                val updatedFormatted = updatedDate?.let { outputFormat.format(it) } ?: sucursal.updated_at

                tvFechas.text = "Creado: $createdFormatted | Actualizado: $updatedFormatted"
            } catch (e: Exception) {
                tvFechas.text = "Creado: ${sucursal.created_at} | Actualizado: ${sucursal.updated_at}"
            }

            // Status indicator
            if (sucursal.es_activa) {
                ivStatus.backgroundTintList =
                    android.content.res.ColorStateList.valueOf(itemView.context.getColor(android.R.color.holo_green_dark))
            } else {
                ivStatus.backgroundTintList =
                    android.content.res.ColorStateList.valueOf(itemView.context.getColor(android.R.color.holo_red_dark))
            }

            // Click listeners
            btnEditar.setOnClickListener { onEditClick(sucursal) }
            btnEliminar.setOnClickListener { onDeleteClick(sucursal) }
        }
    }

    class SucursalDiffCallback : DiffUtil.ItemCallback<Sucursal>() {
        override fun areItemsTheSame(oldItem: Sucursal, newItem: Sucursal): Boolean {
            return oldItem.id_sucursal == newItem.id_sucursal
        }

        override fun areContentsTheSame(oldItem: Sucursal, newItem: Sucursal): Boolean {
            return oldItem == newItem
        }
    }
}

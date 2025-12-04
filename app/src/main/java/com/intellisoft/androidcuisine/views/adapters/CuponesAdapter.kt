package com.intellisoft.androidcuisine.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.data.remote.dto.CuponClienteDto

class CuponesAdapter(
    private val onItemClick: (CuponClienteDto) -> Unit
) : ListAdapter<CuponClienteDto, CuponesAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cupon, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val card: MaterialCardView = itemView.findViewById(R.id.cardCupon)
        private val tvCodigo: TextView = itemView.findViewById(R.id.tvCodigo)
        private val tvDescuento: TextView = itemView.findViewById(R.id.tvDescuento)
        private val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcion)
        private val tvExpiracion: TextView = itemView.findViewById(R.id.tvExpiracion)
        private val tvUsos: TextView = itemView.findViewById(R.id.tvUsos)

        fun bind(cupon: CuponClienteDto) {
            val context = itemView.context

            tvCodigo.text = cupon.codigo
            tvDescuento.text = "${cupon.porcentaje_desc}% OFF"

            if (cupon.nombre_campania.isNullOrEmpty()) {
                tvDescripcion.visibility = View.GONE
            } else {
                tvDescripcion.visibility = View.VISIBLE
                tvDescripcion.text = cupon.nombre_campania
            }

            cupon.fecha_vigencia?.let { fecha ->
                tvExpiracion.visibility = View.VISIBLE
                tvExpiracion.text = "Expira: ${fecha.take(10)}"
            } ?: run {
                tvExpiracion.visibility = View.GONE
            }

            tvUsos.visibility = View.GONE

            card.setOnClickListener {
                onItemClick(cupon)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<CuponClienteDto>() {
        override fun areItemsTheSame(oldItem: CuponClienteDto, newItem: CuponClienteDto): Boolean {
            return oldItem.id_campania_usuario == newItem.id_campania_usuario
        }

        override fun areContentsTheSame(oldItem: CuponClienteDto, newItem: CuponClienteDto): Boolean {
            return oldItem == newItem
        }
    }
}
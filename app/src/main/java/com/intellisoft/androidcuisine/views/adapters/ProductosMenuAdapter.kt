package com.intellisoft.androidcuisine.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.data.remote.dto.ProductoDto

class ProductosMenuAdapter(
    private val onAgregarClick: (ProductoDto) -> Unit
) : ListAdapter<ProductoDto, ProductosMenuAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto_menu, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        private val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcion)
        private val tvPrecio: TextView = itemView.findViewById(R.id.tvPrecio)
        private val tvCategoria: TextView = itemView.findViewById(R.id.tvCategoria)
        private val btnAgregar: MaterialButton = itemView.findViewById(R.id.btnAgregar)

        fun bind(producto: ProductoDto) {
            tvNombre.text = producto.nombre
            tvPrecio.text = "$${String.format("%.2f", producto.precio)}"

            if (producto.descripcion.isNullOrEmpty()) {
                tvDescripcion.visibility = View.GONE
            } else {
                tvDescripcion.visibility = View.VISIBLE
                tvDescripcion.text = producto.descripcion
            }

            tvCategoria.visibility = View.GONE

            btnAgregar.setOnClickListener {
                onAgregarClick(producto)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ProductoDto>() {
        override fun areItemsTheSame(oldItem: ProductoDto, newItem: ProductoDto): Boolean {
            return oldItem.id_producto == newItem.id_producto
        }

        override fun areContentsTheSame(oldItem: ProductoDto, newItem: ProductoDto): Boolean {
            return oldItem == newItem
        }
    }
}
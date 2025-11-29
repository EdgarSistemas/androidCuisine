package com.intellisoft.androidcuisine.views.reserva

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
import java.text.NumberFormat
import java.util.*

class ProductosMenuAdapter(
    private val onAgregar: (ProductoDto) -> Unit
) : ListAdapter<ProductoDto, ProductosMenuAdapter.ProductoViewHolder>(ProductoDiffCallback()) {

    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "MX"))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto_menu, parent, false)
        return ProductoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ProductoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardProducto: MaterialCardView = itemView.findViewById(R.id.cardProducto)
        private val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        private val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcion)
        private val tvPrecio: TextView = itemView.findViewById(R.id.tvPrecio)
        private val btnAgregar: MaterialButton = itemView.findViewById(R.id.btnAgregar)

        fun bind(producto: ProductoDto) {
            tvNombre.text = producto.nombre
            tvPrecio.text = currencyFormat.format(producto.precio)

            if (!producto.descripcion.isNullOrEmpty()) {
                tvDescripcion.text = producto.descripcion
                tvDescripcion.visibility = View.VISIBLE
            } else {
                tvDescripcion.visibility = View.GONE
            }

            btnAgregar.setOnClickListener { onAgregar(producto) }
            cardProducto.setOnClickListener { onAgregar(producto) }
        }
    }

    class ProductoDiffCallback : DiffUtil.ItemCallback<ProductoDto>() {
        override fun areItemsTheSame(oldItem: ProductoDto, newItem: ProductoDto): Boolean {
            return oldItem.id_producto == newItem.id_producto
        }

        override fun areContentsTheSame(oldItem: ProductoDto, newItem: ProductoDto): Boolean {
            return oldItem == newItem
        }
    }
}
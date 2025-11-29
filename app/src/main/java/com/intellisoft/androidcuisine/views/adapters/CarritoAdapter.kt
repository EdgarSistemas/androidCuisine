package com.intellisoft.androidcuisine.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.views.reserva.CarritoItem
import java.text.NumberFormat
import java.util.*

class CarritoAdapter(
    private val onActualizarCantidad: (CarritoItem, Int) -> Unit,
    private val onEliminar: (CarritoItem) -> Unit
) : ListAdapter<CarritoItem, CarritoAdapter.CarritoViewHolder>(CarritoDiffCallback()) {

    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "MX"))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarritoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_carrito, parent, false)
        return CarritoViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarritoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CarritoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        private val tvPrecio: TextView = itemView.findViewById(R.id.tvPrecio)
        private val tvCantidad: TextView = itemView.findViewById(R.id.tvCantidad)
        private val tvSubtotal: TextView = itemView.findViewById(R.id.tvSubtotal)
        private val btnMenos: ImageButton = itemView.findViewById(R.id.btnMenos)
        private val btnMas: ImageButton = itemView.findViewById(R.id.btnMas)
        private val btnEliminar: ImageButton = itemView.findViewById(R.id.btnEliminar)

        fun bind(item: CarritoItem) {
            tvNombre.text = item.nombre
            tvPrecio.text = currencyFormat.format(item.precio)
            tvCantidad.text = item.cantidad.toString()
            tvSubtotal.text = currencyFormat.format(item.precio * item.cantidad)

            btnMenos.setOnClickListener {
                onActualizarCantidad(item, item.cantidad - 1)
            }

            btnMas.setOnClickListener {
                onActualizarCantidad(item, item.cantidad + 1)
            }

            btnEliminar.setOnClickListener {
                onEliminar(item)
            }
        }
    }

    class CarritoDiffCallback : DiffUtil.ItemCallback<CarritoItem>() {
        override fun areItemsTheSame(oldItem: CarritoItem, newItem: CarritoItem): Boolean {
            return oldItem.productoId == newItem.productoId && oldItem.comboId == newItem.comboId
        }

        override fun areContentsTheSame(oldItem: CarritoItem, newItem: CarritoItem): Boolean {
            return oldItem == newItem
        }
    }
}
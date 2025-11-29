package com.intellisoft.androidcuisine.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.views.compras.ArticuloCompra
import java.text.NumberFormat
import java.util.*

class ArticulosCompraAdapter(
    private val articulos: List<ArticuloCompra>,
    private val onDeleteClick: (ArticuloCompra) -> Unit
) : RecyclerView.Adapter<ArticulosCompraAdapter.ArticuloViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticuloViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_articulo_compra, parent, false)
        return ArticuloViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArticuloViewHolder, position: Int) {
        holder.bind(articulos[position])
    }

    override fun getItemCount(): Int = articulos.size

    inner class ArticuloViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNombreInsumo: TextView = itemView.findViewById(R.id.tvNombreInsumo)
        private val tvDetalles: TextView = itemView.findViewById(R.id.tvDetalles)
        private val tvSubtotal: TextView = itemView.findViewById(R.id.tvSubtotal)
        private val btnEliminar: MaterialButton = itemView.findViewById(R.id.btnEliminar)

        fun bind(articulo: ArticuloCompra) {
            tvNombreInsumo.text = articulo.nombreInsumo
            tvDetalles.text = "${articulo.cantidad} ${articulo.presentacion} × ${formatCurrency(articulo.costoUnitario)}"
            tvSubtotal.text = "Subtotal: ${formatCurrency(articulo.subtotal)}"

            btnEliminar.setOnClickListener {
                onDeleteClick(articulo)
            }
        }

        private fun formatCurrency(amount: Double): String {
            val format = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
            return format.format(amount)
        }
    }
}
package com.intellisoft.androidcuisine.views.adapters

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.google.android.material.chip.Chip
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.data.remote.dto.TicketDto

class TicketsAdapter(
    private val onImageClick: (TicketDto) -> Unit
) : RecyclerView.Adapter<TicketsAdapter.TicketViewHolder>() {

    private var items: List<TicketDto> = emptyList()

    fun submitList(newItems: List<TicketDto>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ticket, parent, false) // Necesitas crear este layout
        return TicketViewHolder(view)
    }

    override fun onBindViewHolder(holder: TicketViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    inner class TicketViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivImagen: ImageView = itemView.findViewById(R.id.ivTicketImage)
        private val tvNotas: TextView = itemView.findViewById(R.id.tvTicketNotas)
        private val tvFecha: TextView = itemView.findViewById(R.id.tvTicketFecha)
        private val chipStatus: Chip = itemView.findViewById(R.id.chipTicketStatus)

        fun bind(ticket: TicketDto) {
            tvNotas.text = ticket.notas
            tvFecha.text = ticket.fechaCreacion.take(10) // Solo fecha YYYY-MM-DD

            // Configurar Estatus
            when (ticket.estatus) {
                1 -> {
                    chipStatus.text = "Registrado"
                    chipStatus.setChipBackgroundColorResource(R.color.cuisine_beige_background)
                }
                2 -> {
                    chipStatus.text = "En Proceso"
                    chipStatus.setChipBackgroundColorResource(R.color.cuisine_green_light)
                }
                3 -> {
                    chipStatus.text = "Completado"
                    chipStatus.setChipBackgroundColorResource(R.color.cuisine_green_dark)
                    chipStatus.setTextColor(itemView.context.getColor(R.color.white))
                }
                4 -> {
                    chipStatus.text = "Cancelado"
                    chipStatus.setChipBackgroundColorResource(android.R.color.darker_gray)
                    chipStatus.setTextColor(itemView.context.getColor(R.color.white))
                }
            }

            // Cargar Imagen con Coil o Base64 manual
            if (!ticket.imagenUrl.isNullOrEmpty()) {
                if (ticket.imagenUrl.startsWith("http")) {
                    // Es URL: Usar Coil
                    ivImagen.load(ticket.imagenUrl) {
                        crossfade(true)
                        placeholder(R.drawable.ic_menu_restaurant)
                        error(R.drawable.ic_menu_restaurant)
                        transformations(RoundedCornersTransformation(16f))
                    }
                } else {
                    // Asumimos que es Base64: Decodificar manualmente
                    try {
//                        val decodedBytes = Base64.decode(ticket.imagenUrl, Base64.DEFAULT)
//                        val decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
//                        ivImagen.setImageBitmap(decodedBitmap)

                        // --- CORRECCIÓN: Limpiar el prefijo si existe ---
                        val cleanBase64 = if (ticket.imagenUrl.contains(",")) {
                            // Separa por la coma y toma la segunda parte (el código real)
                            ticket.imagenUrl.split(",")[1]
                        } else {
                            ticket.imagenUrl
                        }
                        // ------------------------------------------------

                        val decodedBytes = Base64.decode(cleanBase64, Base64.DEFAULT)
                        val decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                        ivImagen.setImageBitmap(decodedBitmap)
                    } catch (e: Exception) {
//                        ivImagen.setImageResource(R.drawable.ic_menu_restaurant)
                        ivImagen.setImageResource(R.drawable.ic_menu_restaurant)
                        android.util.Log.e("TicketsAdapter", "Error decodificando imagen: ${e.message}")
                    }
                }

                // Click para ver imagen completa
                ivImagen.setOnClickListener { onImageClick(ticket) }
            } else {
                ivImagen.setImageResource(R.drawable.ic_menu_restaurant)
            }
        }
    }
}
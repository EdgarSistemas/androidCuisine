package com.intellisoft.androidcuisine.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.data.remote.dto.MesaClienteDto

class MesasSeleccionClienteAdapter(
    private val onMesaClick: (MesaClienteDto) -> Unit
) : ListAdapter<MesaClienteDto, MesasSeleccionClienteAdapter.MesaViewHolder>(MesaDiffCallback()) {

    private var mesaSeleccionadaId: Int? = null

    fun setSeleccionada(mesaId: Int?) {
        val oldId = mesaSeleccionadaId
        mesaSeleccionadaId = mesaId

        currentList.forEachIndexed { index, mesa ->
            if (mesa.id_mesa == oldId || mesa.id_mesa == mesaId) {
                notifyItemChanged(index)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MesaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mesa_seleccion_cliente, parent, false)
        return MesaViewHolder(view)
    }

    override fun onBindViewHolder(holder: MesaViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MesaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardMesa: MaterialCardView = itemView.findViewById(R.id.cardMesa)
        private val tvNumero: TextView = itemView.findViewById(R.id.tvNumero)
        private val tvCapacidad: TextView = itemView.findViewById(R.id.tvCapacidad)
        private val tvEstatus: TextView = itemView.findViewById(R.id.tvEstatus)

        fun bind(mesa: MesaClienteDto) {
            val numeroMesa = mesa.codigo_mesa.takeLast(4).trimStart('0').ifEmpty { mesa.id_mesa.toString() }
            tvNumero.text = numeroMesa
            tvCapacidad.text = "${mesa.capacidad} pers."

            val isSeleccionada = mesa.id_mesa == mesaSeleccionadaId
            val isDisponible = mesa.estatus_actual == 1

            when {
                isSeleccionada -> {
                    cardMesa.setCardBackgroundColor(itemView.context.getColor(R.color.cuisine_green_dark))
                    tvNumero.setTextColor(itemView.context.getColor(R.color.white))
                    tvCapacidad.setTextColor(itemView.context.getColor(R.color.white))
                    tvEstatus.visibility = View.GONE
                }
                isDisponible -> {
                    cardMesa.setCardBackgroundColor(itemView.context.getColor(R.color.cuisine_green_light))
                    tvNumero.setTextColor(itemView.context.getColor(R.color.cuisine_dark_text))
                    tvCapacidad.setTextColor(itemView.context.getColor(R.color.cuisine_dark_text))
                    tvEstatus.visibility = View.GONE
                }
                else -> {
                    cardMesa.setCardBackgroundColor(itemView.context.getColor(android.R.color.darker_gray))
                    tvNumero.setTextColor(itemView.context.getColor(R.color.white))
                    tvCapacidad.setTextColor(itemView.context.getColor(R.color.white))
                    tvEstatus.text = mesa.estatus_display ?: "No disponible"
                    tvEstatus.visibility = View.VISIBLE
                }
            }

            cardMesa.alpha = if (isDisponible || isSeleccionada) 1f else 0.6f

            cardMesa.setOnClickListener {
                onMesaClick(mesa)
            }
        }
    }

    class MesaDiffCallback : DiffUtil.ItemCallback<MesaClienteDto>() {
        override fun areItemsTheSame(oldItem: MesaClienteDto, newItem: MesaClienteDto): Boolean {
            return oldItem.id_mesa == newItem.id_mesa
        }

        override fun areContentsTheSame(oldItem: MesaClienteDto, newItem: MesaClienteDto): Boolean {
            return oldItem == newItem
        }
    }
}
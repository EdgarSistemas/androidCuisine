package com.intellisoft.androidcuisine.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.switchmaterial.SwitchMaterial
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.data.remote.dto.CampaniaDto

class CampaniasAdapter(
    private val onSwitchChanged: (CampaniaDto, Boolean) -> Unit
) : ListAdapter<CampaniaDto, CampaniasAdapter.CampaniaViewHolder>(CampaniaDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CampaniaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_campania, parent, false)
        return CampaniaViewHolder(view)
    }

    override fun onBindViewHolder(holder: CampaniaViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CampaniaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNombreCampania: TextView = itemView.findViewById(R.id.tvNombreCampania)
        private val tvCodigo: TextView = itemView.findViewById(R.id.tvCodigo)
        private val tvDescuento: TextView = itemView.findViewById(R.id.tvDescuento)
        private val tvEstadisticas: TextView = itemView.findViewById(R.id.tvEstadisticas)
        private val chipEstado: Chip = itemView.findViewById(R.id.chipEstado)
        private val switchEstado: SwitchMaterial = itemView.findViewById(R.id.switchEstado)

        fun bind(campania: CampaniaDto) {
            tvNombreCampania.text = campania.nombre_campania
            tvCodigo.text = "Código: ${campania.codigo}"
            tvDescuento.text = "${campania.porcentaje_desc.toInt()}% OFF"

            val totalCupones = campania.total_cupones ?: 0
            val usados = campania.total_cupones_usados ?: 0
            val porcentajeUso = if (totalCupones > 0) (usados * 100 / totalCupones) else 0
            tvEstadisticas.text = "Cupones: $totalCupones | Usados: $usados ($porcentajeUso%)"

            configurarEstado(campania.estatus)

            // Evitar loops infinitos
            switchEstado.setOnCheckedChangeListener(null)
            switchEstado.isChecked = campania.estatus == 1
            switchEstado.setOnCheckedChangeListener { _, isChecked ->
                onSwitchChanged(campania, isChecked)
            }
        }

        private fun configurarEstado(estatus: Int) {
            if (estatus == 1) {
                chipEstado.text = "Activa"
                chipEstado.setChipBackgroundColorResource(R.color.cuisine_green_dark)
                chipEstado.setTextColor(itemView.context.getColor(R.color.white))
            } else {
                chipEstado.text = "Inactiva"
                chipEstado.setChipBackgroundColorResource(R.color.cuisine_beige_background)
                chipEstado.setTextColor(itemView.context.getColor(R.color.cuisine_dark_text))
            }
        }
    }

    class CampaniaDiffCallback : DiffUtil.ItemCallback<CampaniaDto>() {
        override fun areItemsTheSame(oldItem: CampaniaDto, newItem: CampaniaDto): Boolean {
            return oldItem.id_campania == newItem.id_campania
        }

        override fun areContentsTheSame(oldItem: CampaniaDto, newItem: CampaniaDto): Boolean {
            return oldItem == newItem
        }
    }
}
package com.intellisoft.androidcuisine.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.data.remote.dto.HorarioDetalle
import java.util.Calendar

class HorarioAdapter(
    private val onRegistrarClick: (HorarioDetalle) -> Unit
) : RecyclerView.Adapter<HorarioAdapter.HorarioViewHolder>() {

    private var detalles: List<HorarioDetalle> = emptyList()

    fun updateData(newDetalles: List<HorarioDetalle>) {
        detalles = newDetalles
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorarioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_horario_dia, parent, false)
        return HorarioViewHolder(view)
    }

    override fun onBindViewHolder(holder: HorarioViewHolder, position: Int) {
        holder.bind(detalles[position])
    }

    override fun getItemCount(): Int = detalles.size

    inner class HorarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDiaSemana: TextView = itemView.findViewById(R.id.tvDiaSemana)
        private val tvHoras: TextView = itemView.findViewById(R.id.tvHoras)
        private val chipEstado: Chip = itemView.findViewById(R.id.chipEstadoAsistencia)
        private val btnRegistrar: Button = itemView.findViewById(R.id.btnRegistrarAsistencia)

        fun bind(detalle: HorarioDetalle) {
            tvDiaSemana.text = getDiaSemana(detalle.dia_semana)
            tvHoras.text = "${formatHora(detalle.hora_inicio)} - ${formatHora(detalle.hora_fin)}"

            // Configurar estado de asistencia
            configurarEstadoAsistencia(detalle)

            // Validar si puede registrar asistencia (solo día actual)
            val esHoy = esDiaActual(detalle.dia_semana)
            btnRegistrar.isEnabled = esHoy
            btnRegistrar.alpha = if (esHoy) 1.0f else 0.5f

            btnRegistrar.setOnClickListener {
                if (esHoy) {
                    onRegistrarClick(detalle)
                }
            }
        }

        private fun configurarEstadoAsistencia(detalle: HorarioDetalle) {
            val diaActual = obtenerDiaActual()

            when {
                detalle.dia_semana > diaActual -> {
                    // Día futuro
                    chipEstado.text = "Pendiente"
                    chipEstado.setChipBackgroundColorResource(R.color.cuisine_beige_background)
                    chipEstado.setTextColor(itemView.context.getColor(R.color.cuisine_dark_text))
                }
                detalle.dia_semana == diaActual -> {
                    // Día actual - aquí deberías verificar con la API si ya registró
                    chipEstado.text = "Hoy"
                    chipEstado.setChipBackgroundColorResource(R.color.cuisine_green_light)
                    chipEstado.setTextColor(itemView.context.getColor(R.color.cuisine_dark_text))
                }
                else -> {
                    // Día pasado - aquí deberías verificar con la API el estado real
                    // Por ahora mostramos como ejemplo
                    chipEstado.text = "Asistencia"
                    chipEstado.setChipBackgroundColorResource(R.color.cuisine_green_dark)
                    chipEstado.setTextColor(itemView.context.getColor(R.color.white))
                }
            }
        }

        private fun esDiaActual(diaSemana: Int): Boolean {
            return diaSemana == obtenerDiaActual()
        }

        private fun obtenerDiaActual(): Int {
            val calendar = Calendar.getInstance()
            val diaSemanaCalendar = calendar.get(Calendar.DAY_OF_WEEK)

            // Convertir de Calendar (Domingo=1) a tu formato (Lunes=1)
            return when (diaSemanaCalendar) {
                Calendar.MONDAY -> 1
                Calendar.TUESDAY -> 2
                Calendar.WEDNESDAY -> 3
                Calendar.THURSDAY -> 4
                Calendar.FRIDAY -> 5
                Calendar.SATURDAY -> 6
                Calendar.SUNDAY -> 7
                else -> 1
            }
        }

        private fun getDiaSemana(dia: Int): String {
            return when (dia) {
                1 -> "Lunes"
                2 -> "Martes"
                3 -> "Miércoles"
                4 -> "Jueves"
                5 -> "Viernes"
                6 -> "Sábado"
                7 -> "Domingo"
                else -> "Día $dia"
            }
        }

        private fun formatHora(hora: String): String {
            return hora.substring(0, 5)
        }
    }
}
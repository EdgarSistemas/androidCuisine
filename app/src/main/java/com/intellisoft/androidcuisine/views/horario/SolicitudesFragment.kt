package com.intellisoft.androidcuisine.views.horario

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.intellisoft.androidcuisine.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SolicitudesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fabNuevaSolicitud: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_solicitudes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewSolicitudes)
        fabNuevaSolicitud = view.findViewById(R.id.fabNuevaSolicitud)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        fabNuevaSolicitud.setOnClickListener {
            showSolicitarVacacionesDialog()
        }
    }

    private fun showSolicitarVacacionesDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_solicitar_vacaciones, null)

        val etFechaInicio = dialogView.findViewById<TextInputEditText>(R.id.etFechaInicio)
        val etFechaFin = dialogView.findViewById<TextInputEditText>(R.id.etFechaFin)

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()

        etFechaInicio.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    calendar.set(year, month, day)
                    etFechaInicio.setText(dateFormat.format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        etFechaFin.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    calendar.set(year, month, day)
                    etFechaFin.setText(dateFormat.format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .create()

        dialogView.findViewById<View>(R.id.btnCancelar).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<View>(R.id.btnEnviarSolicitud).setOnClickListener {
            val fechaInicio = etFechaInicio.text.toString()
            val fechaFin = etFechaFin.text.toString()

            if (fechaInicio.isEmpty() || fechaFin.isEmpty()) {
                Toast.makeText(requireContext(), "Selecciona las fechas", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // TODO: Implementar lógica de envío
            Toast.makeText(requireContext(), "Solicitud enviada", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.show()
    }
}
package com.intellisoft.androidcuisine.views.marketing

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.data.remote.dto.GenerarCampaniaMetricaRequest
import java.text.SimpleDateFormat
import java.util.*

class AsignarCampaniaDialog : DialogFragment() {

    private lateinit var viewModel: MarketingViewModel

    private lateinit var tvClientesSeleccionados: TextView
    private lateinit var etNombreCampania: TextInputEditText
    private lateinit var etCodigoCampania: TextInputEditText
    private lateinit var etPorcentaje: TextInputEditText
    private lateinit var etFechaVigencia: TextInputEditText
    private lateinit var progressBar: ProgressBar
    private lateinit var layoutBotones: LinearLayout
    private lateinit var btnCancelar: MaterialButton
    private lateinit var btnCrearAsignar: MaterialButton

    private var tipoMetrica: String = ""
    private var clientesIds: List<Int> = emptyList()
    private var fechaVigencia: String? = null

    companion object {
        private const val ARG_TIPO_METRICA = "tipo_metrica"
        private const val ARG_CLIENTES_IDS = "clientes_ids"

        fun newInstance(tipoMetrica: String, clientesIds: List<Int>): AsignarCampaniaDialog {
            val dialog = AsignarCampaniaDialog()
            val args = Bundle()
            args.putString(ARG_TIPO_METRICA, tipoMetrica)
            args.putIntegerArrayList(ARG_CLIENTES_IDS, ArrayList(clientesIds))
            dialog.arguments = args
            return dialog
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog)

        tipoMetrica = arguments?.getString(ARG_TIPO_METRICA) ?: ""
        clientesIds = arguments?.getIntegerArrayList(ARG_CLIENTES_IDS) ?: emptyList()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_crear_asignar_campania, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[MarketingViewModel::class.java]

        setupViews(view)
        setupListeners()
        observeViewModel()

        tvClientesSeleccionados.text = "Clientes seleccionados: ${clientesIds.size}"

        // Sugerir código basado en tipo de métrica
        val codigoSugerido = when (tipoMetrica) {
            "vip" -> "VIP${System.currentTimeMillis() % 10000}"
            "frecuentes" -> "FREQ${System.currentTimeMillis() % 10000}"
            "inactivos" -> "VUELVE${System.currentTimeMillis() % 10000}"
            "nuevos" -> "BIENVENIDO${System.currentTimeMillis() % 10000}"
            "canal" -> "CANAL${System.currentTimeMillis() % 10000}"
            else -> "PROMO${System.currentTimeMillis() % 10000}"
        }
        etCodigoCampania.setText(codigoSugerido)
    }

    private fun setupViews(view: View) {
        tvClientesSeleccionados = view.findViewById(R.id.tvClientesSeleccionados)
        etNombreCampania = view.findViewById(R.id.etNombreCampania)
        etCodigoCampania = view.findViewById(R.id.etCodigoCampania)
        etPorcentaje = view.findViewById(R.id.etPorcentaje)
        etFechaVigencia = view.findViewById(R.id.etFechaVigencia)
        progressBar = view.findViewById(R.id.progressBar)
        layoutBotones = view.findViewById(R.id.layoutBotones)
        btnCancelar = view.findViewById(R.id.btnCancelar)
        btnCrearAsignar = view.findViewById(R.id.btnCrearAsignar)
    }

    private fun setupListeners() {
        etFechaVigencia.setOnClickListener {
            showDatePicker()
        }

        btnCancelar.setOnClickListener {
            dismiss()
        }

        btnCrearAsignar.setOnClickListener {
            crearYAsignarCampania()
        }
    }

    private fun observeViewModel() {
        viewModel.generarCampaniaState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is GenerarCampaniaState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    layoutBotones.visibility = View.GONE
                }
                is GenerarCampaniaState.Success -> {
                    progressBar.visibility = View.GONE
                    layoutBotones.visibility = View.VISIBLE

                    val response = state.response
                    val asignados = response.asignaciones?.asignados ?: 0
                    val yaAsignados = response.asignaciones?.ya_asignados ?: 0
                    val codigo = response.campania?.codigo ?: ""

                    Toast.makeText(
                        requireContext(),
                        "Campaña '$codigo' creada.\nAsignados: $asignados | Ya tenían: $yaAsignados",
                        Toast.LENGTH_LONG
                    ).show()

                    viewModel.resetGenerarCampaniaState()
                    viewModel.loadCampanias() // Recargar lista de campañas
                    dismiss()
                }
                is GenerarCampaniaState.Error -> {
                    progressBar.visibility = View.GONE
                    layoutBotones.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    viewModel.resetGenerarCampaniaState()
                }
                else -> {}
            }
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, 30)

        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                calendar.set(year, month, day, 23, 59, 59)

                val displayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                etFechaVigencia.setText(displayFormat.format(calendar.time))

                val apiFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                fechaVigencia = apiFormat.format(calendar.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = System.currentTimeMillis()
        }.show()
    }

    private fun crearYAsignarCampania() {
        val nombre = etNombreCampania.text.toString().trim()
        val codigo = etCodigoCampania.text.toString().trim().uppercase()
        val porcentaje = etPorcentaje.text.toString().toDoubleOrNull()

        if (nombre.isEmpty()) {
            etNombreCampania.error = "Requerido"
            return
        }

        if (codigo.isEmpty()) {
            etCodigoCampania.error = "Requerido"
            return
        }

        if (porcentaje == null || porcentaje <= 0 || porcentaje > 100) {
            etPorcentaje.error = "Ingresa 1-100"
            return
        }

        if (clientesIds.isEmpty()) {
            Toast.makeText(requireContext(), "No hay clientes seleccionados", Toast.LENGTH_SHORT).show()
            return
        }

        val request = GenerarCampaniaMetricaRequest(
            nombre_campania = nombre,
            porcentaje_desc = porcentaje,
            codigo = codigo,
            cliente_ids = clientesIds,
            fecha_vigencia = fechaVigencia
        )

        viewModel.generarCampaniaDesdeMetrica(request)
    }
}
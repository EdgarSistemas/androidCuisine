package com.intellisoft.androidcuisine.views.reserva

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.util.SessionManager
import com.intellisoft.androidcuisine.data.remote.dto.MesaDto
import com.intellisoft.androidcuisine.views.adapters.MesasSeleccionAdapter
import java.text.SimpleDateFormat
import java.util.*

class NuevaReservaFragment : Fragment() {

    private lateinit var viewModel: ReservasViewModel
    private lateinit var sessionManager: SessionManager

    // Views
    private lateinit var cardContador: MaterialCardView
    private lateinit var tvContador: MaterialTextView
    private lateinit var tvMesaSeleccionada: MaterialTextView
    private lateinit var cardMesas: MaterialCardView
    private lateinit var tvMesaInstruccion: MaterialTextView
    private lateinit var recyclerViewMesas: RecyclerView
    private lateinit var progressMesas: ProgressBar
    private lateinit var etFecha: TextInputEditText
    private lateinit var etHoraInicio: TextInputEditText
    private lateinit var actvDuracion: AutoCompleteTextView
    private lateinit var etNotas: TextInputEditText
    private lateinit var btnCancelar: MaterialButton
    private lateinit var btnConfirmar: MaterialButton
    private lateinit var progressBar: ProgressBar

    private lateinit var mesasAdapter: MesasSeleccionAdapter

    private var sucursalId: Int = 0
    private var mesaSeleccionada: MesaDto? = null
    private var fechaSeleccionada: String = ""
    private var horaSeleccionada: String = ""
    private var duracionHoras: Int = 2

    private val duraciones = listOf(
        "1 hora" to 1,
        "2 horas" to 2,
        "3 horas" to 3,
        "4 horas" to 4,
        "5 horas" to 5
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_nueva_reserva, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[ReservasViewModel::class.java]
        sessionManager = SessionManager(requireContext())

        val sucursales = sessionManager.getUserSucursales()
        if (sucursales.isNotEmpty()) {
            sucursalId = sucursales[0].id_sucursal
        }

        setupViews(view)
        setupMesasRecyclerView()
        setupDuracionDropdown()
        setupListeners()
        observeViewModel()

        // Cargar mesas
        viewModel.loadMesas(sucursalId)

        // Fecha por defecto: hoy
        setFechaHoy()

        // INICIAR CONTADOR AL ENTRAR AL FRAGMENT
        viewModel.iniciarContadorSiNoActivo()
        cardContador.visibility = View.VISIBLE
    }

    private fun setupViews(view: View) {
        cardContador = view.findViewById(R.id.cardContador)
        tvContador = view.findViewById(R.id.tvContador)
        tvMesaSeleccionada = view.findViewById(R.id.tvMesaSeleccionada)
        cardMesas = view.findViewById(R.id.cardMesas)
        tvMesaInstruccion = view.findViewById(R.id.tvMesaInstruccion)
        recyclerViewMesas = view.findViewById(R.id.recyclerViewMesas)
        progressMesas = view.findViewById(R.id.progressMesas)
        etFecha = view.findViewById(R.id.etFecha)
        etHoraInicio = view.findViewById(R.id.etHoraInicio)
        actvDuracion = view.findViewById(R.id.actvDuracion)
        etNotas = view.findViewById(R.id.etNotas)
        btnCancelar = view.findViewById(R.id.btnCancelar)
        btnConfirmar = view.findViewById(R.id.btnConfirmar)
        progressBar = view.findViewById(R.id.progressBar)
    }

    private fun setupMesasRecyclerView() {
        mesasAdapter = MesasSeleccionAdapter(
            mesaSeleccionadaId = null,
            onMesaClick = { mesa ->
                if (mesa.estatus_actual == 1) {
                    seleccionarMesa(mesa)
                } else {
                    Toast.makeText(requireContext(), "Mesa no disponible", Toast.LENGTH_SHORT).show()
                }
            }
        )
        recyclerViewMesas.layoutManager = GridLayoutManager(requireContext(), 4)
        recyclerViewMesas.adapter = mesasAdapter
    }

    private fun setupDuracionDropdown() {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            duraciones.map { it.first }
        )
        actvDuracion.setAdapter(adapter)
        actvDuracion.setText("2 horas", false)

        actvDuracion.setOnItemClickListener { _, _, position, _ ->
            duracionHoras = duraciones[position].second
            validarFormulario()
        }
    }

    private fun setupListeners() {
        etFecha.setOnClickListener { showDatePicker() }
        etHoraInicio.setOnClickListener { showTimePicker() }

        btnCancelar.setOnClickListener {
            limpiarYSalir()
        }

        btnConfirmar.setOnClickListener {
            confirmarReserva()
        }
    }

    private fun observeViewModel() {
        viewModel.mesasState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is MesasState.Loading -> {
                    progressMesas.visibility = View.VISIBLE
                }
                is MesasState.Success -> {
                    progressMesas.visibility = View.GONE
                    mesasAdapter.submitList(state.mesas)
                    recyclerViewMesas.visibility = View.VISIBLE
                    tvMesaInstruccion.text = "Selecciona una mesa disponible"
                }
                is MesasState.Empty -> {
                    progressMesas.visibility = View.GONE
                    Toast.makeText(requireContext(), "No hay mesas disponibles", Toast.LENGTH_SHORT).show()
                }
                is MesasState.Error -> {
                    progressMesas.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        viewModel.holdState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is HoldState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                }
                is HoldState.Success -> {
                    progressBar.visibility = View.GONE
                    tvMesaSeleccionada.text = "✓ Mesa ${mesaSeleccionada?.codigo_mesa?.takeLast(6) ?: mesaSeleccionada?.id_mesa} seleccionada"
                    tvMesaSeleccionada.visibility = View.VISIBLE
                    validarFormulario()
                }
                is HoldState.Expired -> {
                    Toast.makeText(
                        requireContext(),
                        "⏱️ Tiempo expirado. Intenta nuevamente.",
                        Toast.LENGTH_LONG
                    ).show()
                    regresarAListaReservas()
                }
                is HoldState.Error -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                }
                else -> {}
            }
        }

        viewModel.tiempoRestante.observe(viewLifecycleOwner) { segundos ->
            if (segundos != null) {
                val minutos = segundos / 60
                val segs = segundos % 60
                tvContador.text = String.format("%02d:%02d", minutos, segs)

                when {
                    segundos <= 30 -> {
                        tvContador.setTextColor(requireContext().getColor(android.R.color.holo_red_dark))
                        cardContador.setCardBackgroundColor(0xFFFFCDD2.toInt())
                    }
                    segundos <= 60 -> {
                        tvContador.setTextColor(requireContext().getColor(android.R.color.holo_orange_dark))
                        cardContador.setCardBackgroundColor(0xFFFFE0B2.toInt())
                    }
                    else -> {
                        tvContador.setTextColor(0xFFE65100.toInt())
                        cardContador.setCardBackgroundColor(0xFFFFF3E0.toInt())
                    }
                }
            }
        }

        viewModel.crearReservaState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is CrearReservaState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    btnConfirmar.isEnabled = false
                }
                is CrearReservaState.Success -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "✓ Reserva creada exitosamente", Toast.LENGTH_LONG).show()
                    viewModel.resetCrearReservaState()

                    // Preparar para nueva reserva
                    prepararParaNuevaReserva()

                    regresarAListaReservas()
                }
                is CrearReservaState.Error -> {
                    progressBar.visibility = View.GONE
                    btnConfirmar.isEnabled = true
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    viewModel.resetCrearReservaState()
                }
                else -> {}
            }
        }
    }

    private fun regresarAListaReservas() {
        (parentFragment as? ReservasFragment)?.let { parent ->
            parent.view?.findViewById<androidx.viewpager2.widget.ViewPager2>(R.id.viewPager)?.currentItem = 0
        }
    }

    private fun seleccionarMesa(mesa: MesaDto) {
        if (fechaSeleccionada.isEmpty() || horaSeleccionada.isEmpty()) {
            Toast.makeText(requireContext(), "Selecciona fecha y hora primero", Toast.LENGTH_SHORT).show()
            return
        }

        if (mesaSeleccionada != null && mesaSeleccionada?.id_mesa != mesa.id_mesa) {
            viewModel.cancelarHoldActivo("Cambio de mesa")
        }

        mesaSeleccionada = mesa
        mesasAdapter.setMesaSeleccionada(mesa.id_mesa)

        val inicio = "$fechaSeleccionada $horaSeleccionada:00"
        viewModel.crearHold(mesa.id_mesa, inicio, duracionHoras)
    }

    private fun setFechaHoy() {
        val calendar = Calendar.getInstance()
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formatApi = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        etFecha.setText(format.format(calendar.time))
        fechaSeleccionada = formatApi.format(calendar.time)
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()

        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                calendar.set(year, month, day)

                val displayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val apiFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                etFecha.setText(displayFormat.format(calendar.time))
                fechaSeleccionada = apiFormat.format(calendar.time)

                validarFormulario()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = System.currentTimeMillis()
        }.show()
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()

        TimePickerDialog(
            requireContext(),
            { _, hour, minute ->
                horaSeleccionada = String.format("%02d:%02d", hour, minute)
                etHoraInicio.setText(horaSeleccionada)
                validarFormulario()
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun validarFormulario() {
        val holdActivo = viewModel.holdActivo.value != null
        val tieneFecha = fechaSeleccionada.isNotEmpty()
        val tieneHora = horaSeleccionada.isNotEmpty()

        btnConfirmar.isEnabled = holdActivo && tieneFecha && tieneHora
    }

    private fun confirmarReserva() {
        val hold = viewModel.holdActivo.value ?: run {
            Toast.makeText(requireContext(), "Selecciona una mesa primero", Toast.LENGTH_SHORT).show()
            return
        }

        val inicio = "$fechaSeleccionada $horaSeleccionada:00"

        val calendar = Calendar.getInstance()
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        calendar.time = inputFormat.parse(inicio) ?: Date()
        calendar.add(Calendar.HOUR_OF_DAY, duracionHoras)
        val finEstimado = inputFormat.format(calendar.time)

        val notas = etNotas.text.toString().trim().ifEmpty { null }

        viewModel.crearReserva(
            holdId = hold.id_hold_mesa,
            inicio = inicio,
            finEstimado = finEstimado,
            toleranciaMin = 15,
            notas = notas
        )
    }

    /**
     * Prepara todo para crear una nueva reserva:
     * - Limpia formulario
     * - Reinicia contador
     * - Recarga mesas
     */
    private fun prepararParaNuevaReserva() {
        // Limpiar datos locales
        mesaSeleccionada = null
        mesasAdapter.setMesaSeleccionada(null)
        tvMesaSeleccionada.visibility = View.GONE
        etNotas.setText("")
        setFechaHoy()
        etHoraInicio.setText("")
        horaSeleccionada = ""
        actvDuracion.setText("2 horas", false)
        duracionHoras = 2
        btnConfirmar.isEnabled = false

        // Resetear colores del contador
        tvContador.text = "03:00"
        tvContador.setTextColor(0xFFE65100.toInt())
        cardContador.setCardBackgroundColor(0xFFFFF3E0.toInt())

        // Resetear ViewModel y reiniciar contador
        viewModel.resetearParaNuevaReserva()

        // Recargar mesas
        viewModel.loadMesas(sucursalId)
    }

    /**
     * Limpia todo y sale del fragment (botón Cancelar)
     */
    private fun limpiarYSalir() {
        if (viewModel.holdActivo.value != null) {
            viewModel.cancelarHoldActivo("Usuario canceló")
        }

        viewModel.resetearParaNuevaReserva()

        // Limpiar datos locales
        mesaSeleccionada = null
        mesasAdapter.setMesaSeleccionada(null)
        tvMesaSeleccionada.visibility = View.GONE
        etNotas.setText("")
        setFechaHoy()
        etHoraInicio.setText("")
        horaSeleccionada = ""
        actvDuracion.setText("2 horas", false)
        duracionHoras = 2
        btnConfirmar.isEnabled = false

        regresarAListaReservas()
    }

    /**
     * Cuando vuelve a ser visible el fragment, reinicia contador si no hay uno activo
     */
    override fun onResume() {
        super.onResume()
        viewModel.iniciarContadorSiNoActivo()
        cardContador.visibility = View.VISIBLE
    }
}
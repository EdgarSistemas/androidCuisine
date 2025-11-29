package com.intellisoft.androidcuisine.views.reserva

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.data.remote.dto.AreaDto
import com.intellisoft.androidcuisine.data.remote.dto.MesaClienteDto
import com.intellisoft.androidcuisine.data.remote.dto.SucursalActivaDto
import com.intellisoft.androidcuisine.views.adapters.MesasSeleccionClienteAdapter
import com.intellisoft.androidcuisine.views.adapters.SucursalesSeleccionAdapter
import java.text.SimpleDateFormat
import java.util.*

class NuevaReservaClienteFragment : Fragment() {

    private lateinit var viewModel: ReservasClienteViewModel

    // Paso 1: Sucursal
    private lateinit var cardPaso1: MaterialCardView
    private lateinit var recyclerSucursales: RecyclerView
    private lateinit var progressSucursales: ProgressBar
    private lateinit var tvSucursalSeleccionada: MaterialTextView

    // Paso 2: Área (opcional) + Mesa
    private lateinit var cardPaso2: MaterialCardView
    private lateinit var spinnerAreas: MaterialAutoCompleteTextView
    private lateinit var recyclerMesas: RecyclerView
    private lateinit var progressMesas: ProgressBar
    private lateinit var tvMesaSeleccionada: MaterialTextView
    private lateinit var layoutPaso2Empty: LinearLayout

    // Paso 3: Fecha y Hora
    private lateinit var cardPaso3: MaterialCardView
    private lateinit var etFecha: TextInputEditText
    private lateinit var etHoraInicio: TextInputEditText
    private lateinit var actvDuracion: MaterialAutoCompleteTextView
    private lateinit var etNotas: TextInputEditText

    // Contador y confirmación
    private lateinit var cardContador: MaterialCardView
    private lateinit var tvContador: MaterialTextView
    private lateinit var btnConfirmar: MaterialButton
    private lateinit var btnCancelar: MaterialButton

    // Adapters
    private lateinit var sucursalesAdapter: SucursalesSeleccionAdapter
    private lateinit var mesasAdapter: MesasSeleccionClienteAdapter

    // Variables de selección
    private var fechaSeleccionada: String = ""
    private var horaSeleccionada: String = ""
    private var duracionHoras: Int = 2
    private var areasLista: List<AreaDto> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_nueva_reserva_cliente, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[ReservasClienteViewModel::class.java]

        setupViews(view)
        setupAdapters()
        setupListeners()
        observeViewModel()

        // Cargar sucursales
        viewModel.loadSucursalesActivas()
        setFechaHoy()
    }

    private fun setupViews(view: View) {
        // Paso 1
        cardPaso1 = view.findViewById(R.id.cardPaso1)
        recyclerSucursales = view.findViewById(R.id.recyclerSucursales)
        progressSucursales = view.findViewById(R.id.progressSucursales)
        tvSucursalSeleccionada = view.findViewById(R.id.tvSucursalSeleccionada)

        // Paso 2
        cardPaso2 = view.findViewById(R.id.cardPaso2)
        spinnerAreas = view.findViewById(R.id.spinnerAreas)
        recyclerMesas = view.findViewById(R.id.recyclerMesas)
        progressMesas = view.findViewById(R.id.progressMesas)
        tvMesaSeleccionada = view.findViewById(R.id.tvMesaSeleccionada)
        layoutPaso2Empty = view.findViewById(R.id.layoutPaso2Empty)

        // Paso 3
        cardPaso3 = view.findViewById(R.id.cardPaso3)
        etFecha = view.findViewById(R.id.etFecha)
        etHoraInicio = view.findViewById(R.id.etHoraInicio)
        actvDuracion = view.findViewById(R.id.actvDuracion)
        etNotas = view.findViewById(R.id.etNotas)

        // Contador y botones
        cardContador = view.findViewById(R.id.cardContador)
        tvContador = view.findViewById(R.id.tvContador)
        btnConfirmar = view.findViewById(R.id.btnConfirmar)
        btnCancelar = view.findViewById(R.id.btnCancelar)

        // Estado inicial
        cardPaso2.visibility = View.GONE
        cardPaso3.visibility = View.GONE
        cardContador.visibility = View.GONE
        btnConfirmar.isEnabled = false
    }

    private fun setupAdapters() {
        // Sucursales
        sucursalesAdapter = SucursalesSeleccionAdapter { sucursal ->
            seleccionarSucursal(sucursal)
        }
        recyclerSucursales.layoutManager = LinearLayoutManager(requireContext())
        recyclerSucursales.adapter = sucursalesAdapter

        // Mesas
        mesasAdapter = MesasSeleccionClienteAdapter { mesa ->
            seleccionarMesa(mesa)
        }
        recyclerMesas.layoutManager = GridLayoutManager(requireContext(), 3)
        recyclerMesas.adapter = mesasAdapter

        // Duración
        val duraciones = arrayOf("1 hora", "2 horas", "3 horas", "4 horas")
        val duracionAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, duraciones)
        actvDuracion.setAdapter(duracionAdapter)
        actvDuracion.setText("2 horas", false)
    }

    private fun setupListeners() {
        // Fecha
        etFecha.setOnClickListener { showDatePicker() }

        // Hora
        etHoraInicio.setOnClickListener { showTimePicker() }

        // Duración
        actvDuracion.setOnItemClickListener { _, _, position, _ ->
            duracionHoras = position + 1
            verificarHoldYCrear()
        }

        // Área
        spinnerAreas.setOnItemClickListener { _, _, position, _ ->
            if (position == 0) {
                viewModel.seleccionarArea(null)
            } else {
                viewModel.seleccionarArea(areasLista[position - 1])
            }
            viewModel.loadMesas()
        }

        // Confirmar
        btnConfirmar.setOnClickListener {
            confirmarReserva()
        }

        // Cancelar
        btnCancelar.setOnClickListener {
            cancelarYSalir()
        }
    }

    private fun observeViewModel() {
        // Sucursales
        viewModel.sucursalesState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is SucursalesClienteState.Loading -> {
                    progressSucursales.visibility = View.VISIBLE
                    recyclerSucursales.visibility = View.GONE
                }
                is SucursalesClienteState.Success -> {
                    progressSucursales.visibility = View.GONE
                    recyclerSucursales.visibility = View.VISIBLE
                    sucursalesAdapter.submitList(state.sucursales)
                }
                is SucursalesClienteState.Empty -> {
                    progressSucursales.visibility = View.GONE
                    Toast.makeText(requireContext(), "No hay sucursales disponibles", Toast.LENGTH_LONG).show()
                }
                is SucursalesClienteState.Error -> {
                    progressSucursales.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        // Sucursal seleccionada
        viewModel.sucursalSeleccionada.observe(viewLifecycleOwner) { sucursal ->
            if (sucursal != null) {
                tvSucursalSeleccionada.text = "✓ ${sucursal.nombre}"
                tvSucursalSeleccionada.visibility = View.VISIBLE
                cardPaso2.visibility = View.VISIBLE
                sucursalesAdapter.setSeleccionada(sucursal.id_sucursal)
            }
        }

        // Áreas
        viewModel.areasState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AreasClienteState.Success -> {
                    areasLista = state.areas
                    val nombres = mutableListOf("Todas las áreas")
                    nombres.addAll(state.areas.map { it.nombre })
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, nombres)
                    spinnerAreas.setAdapter(adapter)
                    spinnerAreas.setText("Todas las áreas", false)
                }
                is AreasClienteState.Empty -> {
                    areasLista = emptyList()
                    spinnerAreas.setAdapter(null)
                }
                else -> {}
            }
        }

        // Mesas
        viewModel.mesasState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is MesasClienteState.Loading -> {
                    progressMesas.visibility = View.VISIBLE
                    recyclerMesas.visibility = View.GONE
                    layoutPaso2Empty.visibility = View.GONE
                }
                is MesasClienteState.Success -> {
                    progressMesas.visibility = View.GONE
                    recyclerMesas.visibility = View.VISIBLE
                    layoutPaso2Empty.visibility = View.GONE
                    mesasAdapter.submitList(state.mesas)
                }
                is MesasClienteState.Empty -> {
                    progressMesas.visibility = View.GONE
                    recyclerMesas.visibility = View.GONE
                    layoutPaso2Empty.visibility = View.VISIBLE
                }
                is MesasClienteState.Error -> {
                    progressMesas.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        // Mesa seleccionada
        viewModel.mesaSeleccionada.observe(viewLifecycleOwner) { mesa ->
            if (mesa != null) {
                tvMesaSeleccionada.text = "✓ ${mesa.codigo_mesa} (${mesa.capacidad} personas)"
                tvMesaSeleccionada.visibility = View.VISIBLE
                cardPaso3.visibility = View.VISIBLE
                mesasAdapter.setSeleccionada(mesa.id_mesa)
                verificarHoldYCrear()
            }
        }

        // Hold
        viewModel.holdState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is HoldClienteState.Success -> {
                    cardContador.visibility = View.VISIBLE
                    btnConfirmar.isEnabled = true
                }
                is HoldClienteState.Expired -> {
                    Toast.makeText(requireContext(), "⏰ Tiempo agotado. Selecciona nuevamente.", Toast.LENGTH_LONG).show()
                    cardContador.visibility = View.GONE
                    btnConfirmar.isEnabled = false
                    viewModel.resetParaNuevaReserva()
                }
                is HoldClienteState.Error -> {
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                }
                else -> {}
            }
        }

        // Contador
        viewModel.tiempoRestante.observe(viewLifecycleOwner) { segundos ->
            if (segundos != null) {
                val minutos = segundos / 60
                val segs = segundos % 60
                tvContador.text = String.format("%02d:%02d", minutos, segs)

                // Colores según tiempo
                when {
                    segundos > 60 -> {
                        tvContador.setTextColor(requireContext().getColor(android.R.color.holo_orange_dark))
                        cardContador.setCardBackgroundColor(0xFFFFF3E0.toInt())
                    }
                    segundos > 30 -> {
                        tvContador.setTextColor(requireContext().getColor(android.R.color.holo_orange_dark))
                        cardContador.setCardBackgroundColor(0xFFFFE0B2.toInt())
                    }
                    else -> {
                        tvContador.setTextColor(requireContext().getColor(android.R.color.holo_red_dark))
                        cardContador.setCardBackgroundColor(0xFFFFCDD2.toInt())
                    }
                }
            }
        }

        // Crear reserva
        viewModel.crearReservaState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is CrearReservaClienteState.Loading -> {
                    btnConfirmar.isEnabled = false
                    btnConfirmar.text = "Creando..."
                }
                is CrearReservaClienteState.Success -> {
                    Toast.makeText(requireContext(), "✓ Reserva creada exitosamente", Toast.LENGTH_SHORT).show()
                    viewModel.resetCrearReservaState()
                    prepararParaNuevaReserva()
                    (parentFragment as? ReservasClienteFragment)?.irAMisReservas()
                }
                is CrearReservaClienteState.Error -> {
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    btnConfirmar.isEnabled = true
                    btnConfirmar.text = "Confirmar Reserva"
                    viewModel.resetCrearReservaState()
                }
                else -> {
                    btnConfirmar.text = "Confirmar Reserva"
                }
            }
        }
    }

    private fun seleccionarSucursal(sucursal: SucursalActivaDto) {
        viewModel.seleccionarSucursal(sucursal)
        viewModel.loadAreas()
        viewModel.loadMesas()
    }

    private fun seleccionarMesa(mesa: MesaClienteDto) {
        if (mesa.estatus_actual != 1) {
            Toast.makeText(requireContext(), "Esta mesa no está disponible", Toast.LENGTH_SHORT).show()
            return
        }
        viewModel.seleccionarMesa(mesa)
    }

    private fun setFechaHoy() {
        val calendar = Calendar.getInstance()
        val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        fechaSeleccionada = formato.format(calendar.time)

        val formatoDisplay = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        etFecha.setText(formatoDisplay.format(calendar.time))
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                fechaSeleccionada = formato.format(calendar.time)

                val formatoDisplay = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                etFecha.setText(formatoDisplay.format(calendar.time))

                verificarHoldYCrear()
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
            { _, hourOfDay, minute ->
                horaSeleccionada = String.format("%02d:%02d", hourOfDay, minute)
                etHoraInicio.setText(horaSeleccionada)
                verificarHoldYCrear()
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            0,
            true
        ).show()
    }

    private fun verificarHoldYCrear() {
        val mesa = viewModel.getMesaValue() ?: return
        if (fechaSeleccionada.isEmpty() || horaSeleccionada.isEmpty()) return

        // Cancelar hold anterior si existe
        viewModel.cancelarHoldActivo()

        // Calcular fechas
        val inicio = "$fechaSeleccionada $horaSeleccionada:00"
        val fin = calcularFin()

        // Iniciar contador y crear hold
        viewModel.iniciarContadorSiNoActivo()
        viewModel.crearHold(inicio, fin)
    }

    private fun calcularFin(): String {
        return try {
            val formato = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val calendar = Calendar.getInstance()
            val fechaInicio = formato.parse("$fechaSeleccionada $horaSeleccionada:00")
            calendar.time = fechaInicio!!
            calendar.add(Calendar.HOUR_OF_DAY, duracionHoras)
            formato.format(calendar.time)
        } catch (e: Exception) {
            "$fechaSeleccionada 23:59:00"
        }
    }

    private fun confirmarReserva() {
        if (viewModel.holdActivo.value == null) {
            Toast.makeText(requireContext(), "Selecciona fecha, hora y mesa", Toast.LENGTH_SHORT).show()
            return
        }

        val inicio = "$fechaSeleccionada $horaSeleccionada:00"
        val finEstimado = calcularFin()
        val notas = etNotas.text.toString().trim().ifEmpty { null }

        viewModel.crearReserva(inicio, finEstimado, notas)
    }

    private fun cancelarYSalir() {
        viewModel.resetParaNuevaReserva()
        prepararParaNuevaReserva()
        (parentFragment as? ReservasClienteFragment)?.irAMisReservas()
    }

    private fun prepararParaNuevaReserva() {
        // Limpiar UI
        tvSucursalSeleccionada.visibility = View.GONE
        tvMesaSeleccionada.visibility = View.GONE
        cardPaso2.visibility = View.GONE
        cardPaso3.visibility = View.GONE
        cardContador.visibility = View.GONE

        etNotas.setText("")
        setFechaHoy()
        etHoraInicio.setText("")
        horaSeleccionada = ""
        actvDuracion.setText("2 horas", false)
        duracionHoras = 2
        btnConfirmar.isEnabled = false
        btnConfirmar.text = "Confirmar Reserva"

        // Reset adapters
        sucursalesAdapter.setSeleccionada(null)
        mesasAdapter.setSeleccionada(null)

        // Reset ViewModel
        viewModel.resetTodo()
        viewModel.loadSucursalesActivas()
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.contadorActivo.value == true) {
            cardContador.visibility = View.VISIBLE
        }
    }

    override fun onPause() {
        super.onPause()
        // No detenemos el contador al salir, se mantiene corriendo
    }
}
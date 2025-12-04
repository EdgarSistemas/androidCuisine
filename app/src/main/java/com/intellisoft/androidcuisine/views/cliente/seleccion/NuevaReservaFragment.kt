package com.intellisoft.androidcuisine.views.cliente.seleccion

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.views.cliente.ClienteViewModel
import com.intellisoft.androidcuisine.views.cliente.HoldState
import com.intellisoft.androidcuisine.views.cliente.CrearReservaState
import com.intellisoft.androidcuisine.views.cliente.seleccion.SeleccionSucursalFragment
import com.intellisoft.androidcuisine.views.cliente.seleccion.SeleccionAreaFragment
import com.intellisoft.androidcuisine.views.cliente.seleccion.SeleccionMesaFragment
import java.text.SimpleDateFormat
import java.util.*

class NuevaReservaFragment : Fragment() {

    private lateinit var viewModel: ClienteViewModel

    // Views - Paso 1: Selección
    private lateinit var cardSeleccion: MaterialCardView
    private lateinit var tvSucursal: TextView
    private lateinit var tvArea: TextView
    private lateinit var tvMesa: TextView
    private lateinit var btnCambiar: MaterialButton

    // Views - Paso 2: Fecha/Hora
    private lateinit var cardFechaHora: MaterialCardView
    private lateinit var btnFecha: MaterialButton
    private lateinit var btnHoraInicio: MaterialButton
    private lateinit var btnHoraFin: MaterialButton
    private lateinit var etNotas: TextInputEditText

    // Views - Paso 3: Confirmar
    private lateinit var cardContador: MaterialCardView
    private lateinit var tvContador: TextView
    private lateinit var btnConfirmar: MaterialButton
    private lateinit var btnCancelar: MaterialButton

    private lateinit var progressBar: ProgressBar
    private lateinit var layoutSeleccionPrimero: LinearLayout

    // Datos seleccionados
    private var fechaSeleccionada: Calendar = Calendar.getInstance()
    private var horaInicio: String = ""
    private var horaFin: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_nueva_reserva_cliente, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[ClienteViewModel::class.java]

        initViews(view)
        setupClickListeners()
        observeViewModel()
        actualizarUI()
    }

    private fun initViews(view: View) {
        // Paso 1
        cardSeleccion = view.findViewById(R.id.cardSeleccion)
        tvSucursal = view.findViewById(R.id.tvSucursal)
        tvArea = view.findViewById(R.id.tvArea)
        tvMesa = view.findViewById(R.id.tvMesa)
        btnCambiar = view.findViewById(R.id.btnCambiar)

        // Paso 2
        cardFechaHora = view.findViewById(R.id.cardFechaHora)
        btnFecha = view.findViewById(R.id.btnFecha)
        btnHoraInicio = view.findViewById(R.id.btnHoraInicio)
        btnHoraFin = view.findViewById(R.id.btnHoraFin)
        etNotas = view.findViewById(R.id.etNotas)

        // Paso 3
        cardContador = view.findViewById(R.id.cardContador)
        tvContador = view.findViewById(R.id.tvContador)
        btnConfirmar = view.findViewById(R.id.btnConfirmar)
        btnCancelar = view.findViewById(R.id.btnCancelar)

        progressBar = view.findViewById(R.id.progressBar)
        layoutSeleccionPrimero = view.findViewById(R.id.layoutSeleccionPrimero)
    }

    private fun setupClickListeners() {
        btnCambiar.setOnClickListener {
            iniciarSeleccion()
        }

        layoutSeleccionPrimero.setOnClickListener {
            iniciarSeleccion()
        }

        btnFecha.setOnClickListener {
            mostrarDatePicker()
        }

        btnHoraInicio.setOnClickListener {
            mostrarTimePickerInicio()
        }

        btnHoraFin.setOnClickListener {
            mostrarTimePickerFin()
        }

        btnConfirmar.setOnClickListener {
            confirmarReserva()
        }

        btnCancelar.setOnClickListener {
            cancelarHold()
        }
    }

    private fun observeViewModel() {
        viewModel.sucursalSeleccionada.observe(viewLifecycleOwner) { actualizarUI() }
        viewModel.areaSeleccionada.observe(viewLifecycleOwner) { actualizarUI() }
        viewModel.mesaSeleccionada.observe(viewLifecycleOwner) { actualizarUI() }

        viewModel.holdTiempoRestante.observe(viewLifecycleOwner) { segundos ->
            val minutos = segundos / 60
            val segs = segundos % 60
            tvContador.text = String.format("%02d:%02d", minutos, segs)
        }

        viewModel.holdExpirado.observe(viewLifecycleOwner) { expirado ->
            if (expirado) {
                Toast.makeText(requireContext(), "El tiempo expiró. Intenta de nuevo.", Toast.LENGTH_LONG).show()
                cardContador.visibility = View.GONE
                viewModel.limpiarSeleccion()
                actualizarUI()
            }
        }

        viewModel.holdState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is HoldState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                }
                is HoldState.Success -> {
                    progressBar.visibility = View.GONE
                    cardContador.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), "Mesa bloqueada. Tienes 3 minutos para confirmar.", Toast.LENGTH_SHORT).show()
                }
                is HoldState.Error -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
                is HoldState.Expired -> {
                    cardContador.visibility = View.GONE
                }
                else -> {
                    progressBar.visibility = View.GONE
                }
            }
        }

        viewModel.crearReservaState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is CrearReservaState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                }
                is CrearReservaState.Success -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "¡Reserva creada exitosamente!", Toast.LENGTH_LONG).show()
                    viewModel.resetCrearReservaState()
                    viewModel.limpiarSeleccion()
                    actualizarUI()
                    // Cambiar al tab de Mis Reservas
                    (parentFragment as? ReservasClienteFragment)?.let {
                        // Ir al tab 0
                    }
                }
                is CrearReservaState.Error -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    viewModel.resetCrearReservaState()
                }
                else -> {}
            }
        }
    }

    private fun actualizarUI() {
        val sucursal = viewModel.sucursalSeleccionada.value
        val area = viewModel.areaSeleccionada.value
        val mesa = viewModel.mesaSeleccionada.value

        if (sucursal != null && area != null && mesa != null) {
            // Mostrar selección completa
            layoutSeleccionPrimero.visibility = View.GONE
            cardSeleccion.visibility = View.VISIBLE
            cardFechaHora.visibility = View.VISIBLE

            tvSucursal.text = "📍 ${sucursal.nombre}"
            tvArea.text = "🏠 ${area.nombre}"
            tvMesa.text = "🪑 ${mesa.codigo_mesa} (${mesa.capacidad} personas)"
        } else {
            // Mostrar botón para seleccionar
            layoutSeleccionPrimero.visibility = View.VISIBLE
            cardSeleccion.visibility = View.GONE
            cardFechaHora.visibility = View.GONE
            cardContador.visibility = View.GONE
        }
    }

    private fun iniciarSeleccion() {
        val seleccionFragment = SeleccionSucursalFragment()
        seleccionFragment.onSucursalSeleccionada = { _ ->
            // Ir a selección de área
            val areaFragment = SeleccionAreaFragment()
            areaFragment.onAreaSeleccionada = { _ ->
                // Ir a selección de mesa
                val mesaFragment = SeleccionMesaFragment()
                mesaFragment.onMesaSeleccionada = { _ ->
                    // Volver a este fragment
                    requireActivity().supportFragmentManager.popBackStack()
                    requireActivity().supportFragmentManager.popBackStack()
                    requireActivity().supportFragmentManager.popBackStack()
                }
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, mesaFragment)
                    .addToBackStack(null)
                    .commit()
            }
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, areaFragment)
                .addToBackStack(null)
                .commit()
        }
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, seleccionFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun mostrarDatePicker() {
        val calendario = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                fechaSeleccionada.set(year, month, day)
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                btnFecha.text = sdf.format(fechaSeleccionada.time)
            },
            calendario.get(Calendar.YEAR),
            calendario.get(Calendar.MONTH),
            calendario.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = System.currentTimeMillis()
        }.show()
    }

    private fun mostrarTimePickerInicio() {
        val calendario = Calendar.getInstance()
        TimePickerDialog(
            requireContext(),
            { _, hour, minute ->
                horaInicio = String.format("%02d:%02d:00", hour, minute)
                btnHoraInicio.text = String.format("%02d:%02d", hour, minute)
            },
            calendario.get(Calendar.HOUR_OF_DAY),
            calendario.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun mostrarTimePickerFin() {
        val calendario = Calendar.getInstance()
        TimePickerDialog(
            requireContext(),
            { _, hour, minute ->
                horaFin = String.format("%02d:%02d:00", hour, minute)
                btnHoraFin.text = String.format("%02d:%02d", hour, minute)

                // Crear hold automáticamente
                crearHold()
            },
            calendario.get(Calendar.HOUR_OF_DAY) + 2,
            calendario.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun crearHold() {
        val mesa = viewModel.mesaSeleccionada.value ?: return
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val fecha = sdf.format(fechaSeleccionada.time)
        val inicio = "$fecha $horaInicio"

        // Calcular horas
        val horasReserva = calcularHoras()

        viewModel.crearHold(
            mesaId = mesa.id_mesa,
            inicio = inicio,
            horas = horasReserva,
            ttlMinutes = 3
        )
    }

    private fun calcularHoras(): Int {
        return try {
            val inicioPartes = horaInicio.split(":")
            val finPartes = horaFin.split(":")
            val horaInicioInt = inicioPartes[0].toInt()
            val horaFinInt = finPartes[0].toInt()
            maxOf(1, horaFinInt - horaInicioInt)
        } catch (e: Exception) {
            2 // Default 2 horas
        }
    }

    private fun confirmarReserva() {
        val hold = viewModel.holdActivo.value
        if (hold == null) {
            Toast.makeText(requireContext(), "Primero selecciona fecha y hora", Toast.LENGTH_SHORT).show()
            return
        }

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val fecha = sdf.format(fechaSeleccionada.time)
        val inicio = "$fecha $horaInicio"
        val finEstimado = "$fecha $horaFin"
        val notas = etNotas.text?.toString()

        viewModel.crearReserva(
            holdId = hold.id_hold_mesa,
            inicio = inicio,
            finEstimado = finEstimado,
            notas = notas
        )
    }

    private fun cancelarHold() {
        val hold = viewModel.holdActivo.value
        hold?.let {
            viewModel.cancelarHold(it.id_hold_mesa)
        }
        cardContador.visibility = View.GONE
    }
}
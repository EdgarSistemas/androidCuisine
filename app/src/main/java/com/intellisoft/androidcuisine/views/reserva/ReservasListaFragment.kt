package com.intellisoft.androidcuisine.views.reserva

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.util.SessionManager
import com.intellisoft.androidcuisine.data.remote.dto.ReservaDto
import com.intellisoft.androidcuisine.views.adapters.ReservasAdapter

class ReservasListaFragment : Fragment() {

    private lateinit var viewModel: ReservasViewModel
    private lateinit var sessionManager: SessionManager

    private lateinit var chipTodas: Chip
    private lateinit var chipProgramadas: Chip
    private lateinit var chipEnCurso: Chip
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var layoutEmpty: LinearLayout
    private lateinit var fabNuevaReserva: FloatingActionButton

    private lateinit var adapter: ReservasAdapter

    private var sucursalId: Int = 0
    private var estatusFiltro: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_reservas_lista, container, false)
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
        setupRecyclerView()
        setupListeners()
        observeViewModel()

        viewModel.loadReservas(sucursalId, estatusFiltro)
    }

    private fun setupViews(view: View) {
        chipTodas = view.findViewById(R.id.chipTodas)
        chipProgramadas = view.findViewById(R.id.chipProgramadas)
        chipEnCurso = view.findViewById(R.id.chipEnCurso)
        recyclerView = view.findViewById(R.id.recyclerViewReservas)
        swipeRefresh = view.findViewById(R.id.swipeRefresh)
        progressBar = view.findViewById(R.id.progressBar)
        layoutEmpty = view.findViewById(R.id.layoutEmpty)
        fabNuevaReserva = view.findViewById(R.id.fabNuevaReserva)
    }

    private fun setupRecyclerView() {
        val esCliente = viewModel.esCliente()

        adapter = ReservasAdapter(
            esCliente = esCliente,
            onIniciar = { reserva ->
                if (!esCliente) {
                    viewModel.iniciarReserva(reserva.id_reserva)
                }
            },
            onCompletar = { reserva ->
                if (!esCliente) {
                    viewModel.completarReserva(reserva.id_reserva)
                }
            },
            onCancelar = { reserva -> showCancelarDialog(reserva) },
            onNoShow = { reserva ->
                if (!esCliente) {
                    showNoShowDialog(reserva)
                }
            }
        )
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun setupListeners() {
        swipeRefresh.setOnRefreshListener {
            viewModel.loadReservas(sucursalId, estatusFiltro)
        }

        chipTodas.setOnClickListener {
            estatusFiltro = null
            viewModel.loadReservas(sucursalId, estatusFiltro)
        }

        chipProgramadas.setOnClickListener {
            estatusFiltro = 1
            viewModel.loadReservas(sucursalId, estatusFiltro)
        }

        chipEnCurso.setOnClickListener {
            estatusFiltro = 2
            viewModel.loadReservas(sucursalId, estatusFiltro)
        }

        fabNuevaReserva.setOnClickListener {
            (parentFragment as? ReservasFragment)?.let { parent ->
                parent.view?.findViewById<androidx.viewpager2.widget.ViewPager2>(R.id.viewPager)?.currentItem = 1
            }
        }
    }

    private fun observeViewModel() {
        viewModel.reservasState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ReservasState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                    layoutEmpty.visibility = View.GONE
                }
                is ReservasState.Success -> {
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    layoutEmpty.visibility = View.GONE
                    adapter.submitList(state.reservas)
                    swipeRefresh.isRefreshing = false
                }
                is ReservasState.Empty -> {
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                    layoutEmpty.visibility = View.VISIBLE
                    swipeRefresh.isRefreshing = false
                }
                is ReservasState.Error -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    swipeRefresh.isRefreshing = false
                }
            }
        }

        viewModel.accionReservaState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AccionReservaState.Success -> {
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    viewModel.resetAccionReservaState()
                    viewModel.loadReservas(sucursalId, estatusFiltro)
                }
                is AccionReservaState.Error -> {
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    viewModel.resetAccionReservaState()
                }
                else -> {}
            }
        }
    }

    private fun showCancelarDialog(reserva: ReservaDto) {
        val esCliente = viewModel.esCliente()
        val title = if (esCliente) "Cancelar mi Reserva" else "Cancelar Reserva"
        val message = if (esCliente) "¿Estás seguro de cancelar tu reserva?" else "¿Estás seguro de cancelar esta reserva?"

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
            .setPositiveButton("Sí, cancelar") { _, _ ->
                val motivo = if (esCliente) "Cancelada por cliente" else "Cancelada por recepción"
                viewModel.cancelarReserva(reserva.id_reserva, motivo)
            }
            .show()
    }

    private fun showNoShowDialog(reserva: ReservaDto) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Marcar No Show")
            .setMessage("¿El cliente no se presentó?")
            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
            .setPositiveButton("Sí, no show") { _, _ ->
                viewModel.marcarNoShow(reserva.id_reserva)
            }
            .show()
    }
}
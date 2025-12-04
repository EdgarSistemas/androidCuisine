package com.intellisoft.androidcuisine.views.cliente.seleccion

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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.data.remote.dto.ReservaClienteDto
import com.intellisoft.androidcuisine.views.adapters.MisReservasAdapter
import com.intellisoft.androidcuisine.views.cliente.AccionReservaState
import com.intellisoft.androidcuisine.views.cliente.CancelarReservaState
import com.intellisoft.androidcuisine.views.cliente.ClienteViewModel
import com.intellisoft.androidcuisine.views.cliente.MisReservasState
import com.intellisoft.androidcuisine.views.cliente.SucursalesState
import com.intellisoft.androidcuisine.views.cliente.pedidos.PedidoReservaFragment

class MisReservasFragment : Fragment() {

    private lateinit var viewModel: ClienteViewModel

    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var layoutEmpty: LinearLayout
    private lateinit var layoutError: LinearLayout

    private lateinit var adapter: MisReservasAdapter

    // Para evitar múltiples diálogos
    private var dialogoSucursalMostrado = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mis_reservas_cliente, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[ClienteViewModel::class.java]

        initViews(view)
        setupRecyclerView()
        setupSwipeRefresh()
        observeViewModel()

        cargarReservas()
    }

    private fun initViews(view: View) {
        swipeRefresh = view.findViewById(R.id.swipeRefresh)
        recyclerView = view.findViewById(R.id.recyclerView)
        progressBar = view.findViewById(R.id.progressBar)
        layoutEmpty = view.findViewById(R.id.layoutEmpty)
        layoutError = view.findViewById(R.id.layoutError)
    }

    private fun setupRecyclerView() {
        adapter = MisReservasAdapter(
            onIniciarClick = { reserva -> confirmarIniciar(reserva) },
            onCompletarClick = { reserva -> confirmarCompletar(reserva) },
            onCancelarClick = { reserva -> confirmarCancelar(reserva) },
            onOrdenarClick = { reserva -> navegarAOrdenar(reserva) }
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun navegarAOrdenar(reserva: ReservaClienteDto) {
        val sucursalId = viewModel.sucursalSeleccionada.value?.id_sucursal

        if (sucursalId == null || sucursalId == 0) {
            // No hay sucursal guardada, mostrar diálogo para seleccionar
            mostrarDialogoSeleccionSucursal(reserva)
            return
        }

        abrirPedidoReserva(reserva.id_reserva, sucursalId)
    }

    private fun abrirPedidoReserva(reservaId: Int, sucursalId: Int) {
        val pedidoFragment = PedidoReservaFragment.newInstance(
            reservaId = reservaId,
            sucursalId = sucursalId
        )
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, pedidoFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun mostrarDialogoSeleccionSucursal(reserva: ReservaClienteDto) {
        if (dialogoSucursalMostrado) return
        dialogoSucursalMostrado = true

        viewModel.cargarSucursales()

        viewModel.sucursalesState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is SucursalesState.Success -> {
                    if (!dialogoSucursalMostrado) return@observe
                    dialogoSucursalMostrado = false

                    val sucursales = state.sucursales
                    if (sucursales.isEmpty()) {
                        Toast.makeText(requireContext(), "No hay sucursales disponibles", Toast.LENGTH_SHORT).show()
                        return@observe
                    }

                    val nombres = sucursales.map { it.nombre }.toTypedArray()

                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("🏪 Selecciona la sucursal")
                        .setItems(nombres) { _, which ->
                            val sucursalSeleccionada = sucursales[which]
                            viewModel.seleccionarSucursal(sucursalSeleccionada)
                            abrirPedidoReserva(reserva.id_reserva, sucursalSeleccionada.id_sucursal)
                        }
                        .setNegativeButton("Cancelar") { _, _ ->
                            dialogoSucursalMostrado = false
                        }
                        .setOnCancelListener {
                            dialogoSucursalMostrado = false
                        }
                        .show()
                }
                is SucursalesState.Error -> {
                    dialogoSucursalMostrado = false
                    Toast.makeText(requireContext(), "Error al cargar sucursales: ${state.message}", Toast.LENGTH_SHORT).show()
                }
                is SucursalesState.Loading -> {
                    // Mostrar loading si quieres
                }
                else -> {}
            }
        }
    }

    private fun setupSwipeRefresh() {
        swipeRefresh.setColorSchemeResources(R.color.cuisine_orange)
        swipeRefresh.setOnRefreshListener {
            cargarReservas()
        }
    }

    private fun observeViewModel() {
        // Observar lista de reservas
        viewModel.misReservasState.observe(viewLifecycleOwner) { state ->
            swipeRefresh.isRefreshing = false

            when (state) {
                is MisReservasState.Idle -> hideAll()
                is MisReservasState.Loading -> showLoading()
                is MisReservasState.Empty -> showEmpty()
                is MisReservasState.Success -> showContent(state.reservas)
                is MisReservasState.Error -> showError(state.message)
            }
        }

        // Observar acciones de iniciar/completar
        viewModel.accionReservaState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AccionReservaState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                }
                is AccionReservaState.Success -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "✅ ${state.message}", Toast.LENGTH_SHORT).show()
                    viewModel.resetAccionReservaState()
                }
                is AccionReservaState.Error -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "❌ ${state.message}", Toast.LENGTH_LONG).show()
                    viewModel.resetAccionReservaState()
                }
                else -> {
                    progressBar.visibility = View.GONE
                }
            }
        }

        // Observar acción de cancelar
        viewModel.cancelarReservaState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is CancelarReservaState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                }
                is CancelarReservaState.Success -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "✅ Reserva cancelada", Toast.LENGTH_SHORT).show()
                    viewModel.resetCancelarReservaState()
                    cargarReservas()
                }
                is CancelarReservaState.Error -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "❌ ${state.message}", Toast.LENGTH_LONG).show()
                    viewModel.resetCancelarReservaState()
                }
                else -> {
                    progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun cargarReservas() {
        viewModel.cargarMisReservas()
    }

    private fun confirmarIniciar(reserva: ReservaClienteDto) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Iniciar Reserva")
            .setMessage("¿Confirmas que llegaste a tu mesa?\n\nReserva #${reserva.id_reserva}")
            .setPositiveButton("Sí, llegué") { _, _ ->
                viewModel.iniciarReserva(reserva.id_reserva)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun confirmarCompletar(reserva: ReservaClienteDto) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Completar Reserva")
            .setMessage("¿Terminaste tu visita?\n\nReserva #${reserva.id_reserva}")
            .setPositiveButton("Sí, terminé") { _, _ ->
                viewModel.completarReserva(reserva.id_reserva)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun confirmarCancelar(reserva: ReservaClienteDto) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Cancelar Reserva")
            .setMessage("¿Estás seguro de cancelar esta reserva?\n\nReserva #${reserva.id_reserva}")
            .setPositiveButton("Sí, cancelar") { _, _ ->
                viewModel.cancelarReserva(reserva.id_reserva)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun hideAll() {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.GONE
        layoutEmpty.visibility = View.GONE
        layoutError.visibility = View.GONE
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        layoutEmpty.visibility = View.GONE
        layoutError.visibility = View.GONE
    }

    private fun showEmpty() {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.GONE
        layoutEmpty.visibility = View.VISIBLE
        layoutError.visibility = View.GONE
    }

    private fun showContent(reservas: List<ReservaClienteDto>) {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        layoutEmpty.visibility = View.GONE
        layoutError.visibility = View.GONE
        adapter.submitList(reservas)
    }

    private fun showError(message: String) {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.GONE
        layoutEmpty.visibility = View.GONE
        layoutError.visibility = View.VISIBLE
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
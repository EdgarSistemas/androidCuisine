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
import com.intellisoft.androidcuisine.data.remote.dto.ReservaClienteDto
import com.intellisoft.androidcuisine.views.adapters.MisReservasClienteAdapter

class MisReservasClienteFragment : Fragment() {

    private lateinit var viewModel: ReservasClienteViewModel

    private lateinit var chipTodas: Chip
    private lateinit var chipConfirmadas: Chip
    private lateinit var chipCompletadas: Chip
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var layoutEmpty: LinearLayout
    private lateinit var fabNuevaReserva: FloatingActionButton

    private lateinit var adapter: MisReservasClienteAdapter

    private var estadoFiltro: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mis_reservas_cliente, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[ReservasClienteViewModel::class.java]

        setupViews(view)
        setupRecyclerView()
        setupListeners()
        observeViewModel()

        viewModel.loadMisReservas(estadoFiltro)
    }

    private fun setupViews(view: View) {
        chipTodas = view.findViewById(R.id.chipTodas)
        chipConfirmadas = view.findViewById(R.id.chipConfirmadas)
        chipCompletadas = view.findViewById(R.id.chipCompletadas)
        recyclerView = view.findViewById(R.id.recyclerViewReservas)
        swipeRefresh = view.findViewById(R.id.swipeRefresh)
        progressBar = view.findViewById(R.id.progressBar)
        layoutEmpty = view.findViewById(R.id.layoutEmpty)
        fabNuevaReserva = view.findViewById(R.id.fabNuevaReserva)
    }

    private fun setupRecyclerView() {
        adapter = MisReservasClienteAdapter(
            onCancelar = { reserva -> showCancelarDialog(reserva) }
        )
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun setupListeners() {
        swipeRefresh.setOnRefreshListener {
            viewModel.loadMisReservas(estadoFiltro)
        }
        swipeRefresh.setColorSchemeResources(R.color.cuisine_green_dark)

        chipTodas.setOnClickListener {
            estadoFiltro = null
            actualizarChips()
            viewModel.loadMisReservas(estadoFiltro)
        }

        chipConfirmadas.setOnClickListener {
            estadoFiltro = 0
            actualizarChips()
            viewModel.loadMisReservas(estadoFiltro)
        }

        chipCompletadas.setOnClickListener {
            estadoFiltro = 2
            actualizarChips()
            viewModel.loadMisReservas(estadoFiltro)
        }

        fabNuevaReserva.setOnClickListener {
            (parentFragment as? ReservasClienteFragment)?.irANuevaReserva()
        }
    }

    private fun actualizarChips() {
        chipTodas.isChecked = estadoFiltro == null
        chipConfirmadas.isChecked = estadoFiltro == 0
        chipCompletadas.isChecked = estadoFiltro == 2
    }

    private fun observeViewModel() {
        viewModel.misReservasState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is MisReservasState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                    layoutEmpty.visibility = View.GONE
                }
                is MisReservasState.Success -> {
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    layoutEmpty.visibility = View.GONE
                    adapter.submitList(state.reservas)
                    swipeRefresh.isRefreshing = false
                }
                is MisReservasState.Empty -> {
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                    layoutEmpty.visibility = View.VISIBLE
                    swipeRefresh.isRefreshing = false
                }
                is MisReservasState.Error -> {
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                    layoutEmpty.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    swipeRefresh.isRefreshing = false
                }
            }
        }

        viewModel.cancelarReservaState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is CancelarReservaClienteState.Success -> {
                    Toast.makeText(requireContext(), "✓ Reserva cancelada", Toast.LENGTH_SHORT).show()
                    viewModel.resetCancelarReservaState()
                    viewModel.loadMisReservas(estadoFiltro)
                }
                is CancelarReservaClienteState.Error -> {
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    viewModel.resetCancelarReservaState()
                }
                else -> {}
            }
        }
    }

    private fun showCancelarDialog(reserva: ReservaClienteDto) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Cancelar Reserva")
            .setMessage("¿Estás seguro de cancelar esta reserva?")
            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
            .setPositiveButton("Sí, cancelar") { _, _ ->
                viewModel.cancelarReserva(reserva.id_reserva, "Cancelada por cliente")
            }
            .show()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadMisReservas(estadoFiltro)
    }
}
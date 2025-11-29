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
import com.intellisoft.androidcuisine.data.remote.dto.PedidoDetalleDto
import com.intellisoft.androidcuisine.views.adapters.MisPedidosClienteAdapter

class MisPedidosClienteFragment : Fragment() {

    private lateinit var viewModel: PedidosClienteViewModel

    private lateinit var chipTodos: Chip
    private lateinit var chipActivos: Chip
    private lateinit var chipCompletados: Chip
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var layoutEmpty: LinearLayout
    private lateinit var fabNuevoPedido: FloatingActionButton

    private lateinit var adapter: MisPedidosClienteAdapter

    private var estadoFiltro: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mis_pedidos_cliente, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[PedidosClienteViewModel::class.java]

        setupViews(view)
        setupRecyclerView()
        setupListeners()
        observeViewModel()

        viewModel.loadMisPedidos(estadoFiltro)
    }

    private fun setupViews(view: View) {
        chipTodos = view.findViewById(R.id.chipTodos)
        chipActivos = view.findViewById(R.id.chipActivos)
        chipCompletados = view.findViewById(R.id.chipCompletados)
        recyclerView = view.findViewById(R.id.recyclerViewPedidos)
        swipeRefresh = view.findViewById(R.id.swipeRefresh)
        progressBar = view.findViewById(R.id.progressBar)
        layoutEmpty = view.findViewById(R.id.layoutEmpty)
        fabNuevoPedido = view.findViewById(R.id.fabNuevoPedido)
    }

    private fun setupRecyclerView() {
        adapter = MisPedidosClienteAdapter(
            onVerDetalle = { pedido -> verDetallePedido(pedido) },
            onCancelar = { pedido -> showCancelarDialog(pedido) }
        )
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun setupListeners() {
        swipeRefresh.setOnRefreshListener {
            viewModel.loadMisPedidos(estadoFiltro)
        }
        swipeRefresh.setColorSchemeResources(R.color.cuisine_green_dark)

        chipTodos.setOnClickListener {
            estadoFiltro = null
            actualizarChips()
            viewModel.loadMisPedidos(estadoFiltro)
        }

        chipActivos.setOnClickListener {
            estadoFiltro = 0 // Iniciado
            actualizarChips()
            viewModel.loadMisPedidos(estadoFiltro)
        }

        chipCompletados.setOnClickListener {
            estadoFiltro = 5 // Pagado
            actualizarChips()
            viewModel.loadMisPedidos(estadoFiltro)
        }

        fabNuevoPedido.setOnClickListener {
            (parentFragment as? PedidosClienteFragment)?.irANuevoPedido()
        }
    }

    private fun actualizarChips() {
        chipTodos.isChecked = estadoFiltro == null
        chipActivos.isChecked = estadoFiltro == 0
        chipCompletados.isChecked = estadoFiltro == 5
    }

    private fun observeViewModel() {
        viewModel.misPedidosState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is MisPedidosState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                    layoutEmpty.visibility = View.GONE
                }
                is MisPedidosState.Success -> {
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    layoutEmpty.visibility = View.GONE
                    adapter.submitList(state.pedidos)
                    swipeRefresh.isRefreshing = false
                }
                is MisPedidosState.Empty -> {
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                    layoutEmpty.visibility = View.VISIBLE
                    swipeRefresh.isRefreshing = false
                }
                is MisPedidosState.Error -> {
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                    layoutEmpty.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    swipeRefresh.isRefreshing = false
                }
            }
        }

        viewModel.accionPedidoState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AccionPedidoClienteState.Success -> {
                    Toast.makeText(requireContext(), "✓ Pedido cancelado", Toast.LENGTH_SHORT).show()
                    viewModel.resetAccionPedidoState()
                    viewModel.loadMisPedidos(estadoFiltro)
                }
                is AccionPedidoClienteState.Error -> {
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    viewModel.resetAccionPedidoState()
                }
                else -> {}
            }
        }
    }

    private fun verDetallePedido(pedido: PedidoDetalleDto) {
        val fragment = DetallePedidoClienteFragment.newInstance(pedido.id_pedido)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun showCancelarDialog(pedido: PedidoDetalleDto) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Cancelar Pedido")
            .setMessage("¿Estás seguro de cancelar este pedido?")
            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
            .setPositiveButton("Sí, cancelar") { _, _ ->
                viewModel.cancelarPedido(pedido.id_pedido, "Cancelado por cliente")
            }
            .show()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadMisPedidos(estadoFiltro)
    }
}
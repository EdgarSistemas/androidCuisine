package com.intellisoft.androidcuisine.views.cliente.pedidos

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
import com.google.android.material.chip.ChipGroup
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.data.remote.dto.PedidoClienteDto
import com.intellisoft.androidcuisine.views.adapters.MisPedidosAdapter
import com.intellisoft.androidcuisine.views.cliente.ClienteViewModel
import com.intellisoft.androidcuisine.views.cliente.MisPedidosState

class MisPedidosFragment : Fragment() {

    private lateinit var viewModel: ClienteViewModel

    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var chipGroup: ChipGroup
    private lateinit var chipTodos: Chip
    private lateinit var chipActivos: Chip
    private lateinit var chipCompletados: Chip
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var layoutEmpty: LinearLayout
    private lateinit var layoutError: LinearLayout

    private lateinit var adapter: MisPedidosAdapter

    private var filtroActual: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mis_pedidos_cliente, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[ClienteViewModel::class.java]

        initViews(view)
        setupRecyclerView()
        setupChipFilters()
        setupSwipeRefresh()
        observeViewModel()

        cargarPedidos()
    }

    private fun initViews(view: View) {
        swipeRefresh = view.findViewById(R.id.swipeRefresh)
        chipGroup = view.findViewById(R.id.chipGroup)
        chipTodos = view.findViewById(R.id.chipTodos)
        chipActivos = view.findViewById(R.id.chipActivos)
        chipCompletados = view.findViewById(R.id.chipCompletados)
        recyclerView = view.findViewById(R.id.recyclerView)
        progressBar = view.findViewById(R.id.progressBar)
        layoutEmpty = view.findViewById(R.id.layoutEmpty)
        layoutError = view.findViewById(R.id.layoutError)
    }

    private fun setupRecyclerView() {
        adapter = MisPedidosAdapter { pedido ->
            navegarADetalle(pedido)
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun setupChipFilters() {
        chipTodos.setOnClickListener {
            filtroActual = null
            cargarPedidos()
        }

        chipActivos.setOnClickListener {
            filtroActual = 1 // Pendiente/En preparación
            cargarPedidos()
        }

        chipCompletados.setOnClickListener {
            filtroActual = 4 // Entregado
            cargarPedidos()
        }
    }

    private fun setupSwipeRefresh() {
        swipeRefresh.setColorSchemeResources(R.color.cuisine_orange)
        swipeRefresh.setOnRefreshListener {
            cargarPedidos()
        }
    }

    private fun observeViewModel() {
        viewModel.misPedidosState.observe(viewLifecycleOwner) { state ->
            swipeRefresh.isRefreshing = false

            when (state) {
                is MisPedidosState.Idle -> hideAll()
                is MisPedidosState.Loading -> showLoading()
                is MisPedidosState.Empty -> showEmpty()
                is MisPedidosState.Success -> showContent(state.pedidos)
                is MisPedidosState.Error -> showError(state.message)
            }
        }
    }

    private fun cargarPedidos() {
        viewModel.cargarMisPedidos(filtroActual)
    }

    private fun navegarADetalle(pedido: PedidoClienteDto) {
        viewModel.cargarDetallePedido(pedido.id_pedido)

        val fragment = DetallePedidoFragment()
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
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

    private fun showContent(pedidos: List<PedidoClienteDto>) {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        layoutEmpty.visibility = View.GONE
        layoutError.visibility = View.GONE
        adapter.submitList(pedidos)
    }

    private fun showError(message: String) {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.GONE
        layoutEmpty.visibility = View.GONE
        layoutError.visibility = View.VISIBLE
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
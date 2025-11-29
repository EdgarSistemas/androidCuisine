package com.intellisoft.androidcuisine.views.cocina

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
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.util.SessionManager
import com.intellisoft.androidcuisine.views.adapters.PedidosCocinaAdapter

class PedidosCocinaFragment : Fragment() {

    private lateinit var viewModel: CocinaViewModel
    private lateinit var sessionManager: SessionManager

    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var layoutEmpty: LinearLayout

    private lateinit var adapter: PedidosCocinaAdapter

    private var sucursalId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pedidos_cocina, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[CocinaViewModel::class.java]
        sessionManager = SessionManager(requireContext())

        val sucursales = sessionManager.getUserSucursales()
        if (sucursales.isNotEmpty()) {
            sucursalId = sucursales[0].id_sucursal
        }

        setupViews(view)
        setupRecyclerView()
        setupListeners()
        observeViewModel()

        viewModel.loadPedidosEnCocina(sucursalId)
    }

    private fun setupViews(view: View) {
        recyclerView = view.findViewById(R.id.recyclerViewPedidos)
        swipeRefresh = view.findViewById(R.id.swipeRefresh)
        progressBar = view.findViewById(R.id.progressBar)
        layoutEmpty = view.findViewById(R.id.layoutEmpty)
    }

    private fun setupRecyclerView() {
        adapter = PedidosCocinaAdapter { itemId ->
            viewModel.marcarItemListo(itemId)
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun setupListeners() {
        swipeRefresh.setOnRefreshListener {
            viewModel.loadPedidosEnCocina(sucursalId)
        }
    }

    private fun observeViewModel() {
        viewModel.pedidosCocinaState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PedidosCocinaState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                    layoutEmpty.visibility = View.GONE
                }
                is PedidosCocinaState.Success -> {
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    layoutEmpty.visibility = View.GONE
                    adapter.submitList(state.pedidos)
                    swipeRefresh.isRefreshing = false
                }
                is PedidosCocinaState.Empty -> {
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                    layoutEmpty.visibility = View.VISIBLE
                    swipeRefresh.isRefreshing = false
                }
                is PedidosCocinaState.Error -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    swipeRefresh.isRefreshing = false
                }
            }
        }

        viewModel.marcarListoState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is MarcarListoState.Success -> {
                    val response = state.response
                    if (response.pedido_auto_pagado) {
                        Toast.makeText(
                            requireContext(),
                            "✓ Pedido Takeaway AUTO-PAGADO",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "✓ Item listo",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    viewModel.resetMarcarListoState()
                    viewModel.loadPedidosEnCocina(sucursalId)
                }
                is MarcarListoState.Error -> {
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    viewModel.resetMarcarListoState()
                }
                else -> {}
            }
        }
    }
}
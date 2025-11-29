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
import com.intellisoft.androidcuisine.views.adapters.ItemsPendientesAdapter

class ItemsPendientesFragment : Fragment() {

    private lateinit var viewModel: CocinaViewModel
    private lateinit var sessionManager: SessionManager

    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var layoutEmpty: LinearLayout

    private lateinit var adapter: ItemsPendientesAdapter

    private var sucursalId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_items_pendientes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[CocinaViewModel::class.java]
        sessionManager = SessionManager(requireContext())

        // Obtener sucursal del usuario
        val sucursales = sessionManager.getUserSucursales()
        if (sucursales.isNotEmpty()) {
            sucursalId = sucursales[0].id_sucursal
        }

        setupViews(view)
        setupRecyclerView()
        setupListeners()
        observeViewModel()

        viewModel.loadItemsPendientes(sucursalId)
    }

    private fun setupViews(view: View) {
        recyclerView = view.findViewById(R.id.recyclerViewItems)
        swipeRefresh = view.findViewById(R.id.swipeRefresh)
        progressBar = view.findViewById(R.id.progressBar)
        layoutEmpty = view.findViewById(R.id.layoutEmpty)
    }

    private fun setupRecyclerView() {
        adapter = ItemsPendientesAdapter { item ->
            viewModel.marcarItemListo(item.id_pedido_item)
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun setupListeners() {
        swipeRefresh.setOnRefreshListener {
            viewModel.loadItemsPendientes(sucursalId)
        }
    }

    private fun observeViewModel() {
        viewModel.itemsPendientesState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ItemsPendientesState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                    layoutEmpty.visibility = View.GONE
                }
                is ItemsPendientesState.Success -> {
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    layoutEmpty.visibility = View.GONE
                    adapter.submitList(state.items)
                    swipeRefresh.isRefreshing = false
                }
                is ItemsPendientesState.Empty -> {
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                    layoutEmpty.visibility = View.VISIBLE
                    swipeRefresh.isRefreshing = false
                }
                is ItemsPendientesState.Error -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    swipeRefresh.isRefreshing = false
                }
            }
        }

        viewModel.marcarListoState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is MarcarListoState.Loading -> {
                    // Podría mostrar loading en el item
                }
                is MarcarListoState.Success -> {
                    val response = state.response
                    if (response.pedido_auto_pagado) {
                        Toast.makeText(
                            requireContext(),
                            "✓ Listo - Pedido Takeaway AUTO-PAGADO",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "✓ Item marcado como listo",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    viewModel.resetMarcarListoState()
                    viewModel.loadItemsPendientes(sucursalId)
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
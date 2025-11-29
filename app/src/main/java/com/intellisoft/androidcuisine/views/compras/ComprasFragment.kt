package com.intellisoft.androidcuisine.views.compras

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.util.SessionManager
import com.intellisoft.androidcuisine.views.adapters.ComprasAdapter

class ComprasFragment : Fragment() {

    private lateinit var viewModel: ComprasViewModel
    private lateinit var sessionManager: SessionManager

    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView
    private lateinit var fabNuevaCompra: FloatingActionButton

    private lateinit var adapter: ComprasAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_compras, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[ComprasViewModel::class.java]
        sessionManager = SessionManager.getInstance(requireContext())

        setupViews(view)
        setupRecyclerView()
        setupListeners()
        observeViewModel()

        loadCompras()
    }

    private fun setupViews(view: View) {
        recyclerView = view.findViewById(R.id.recyclerViewCompras)
        swipeRefresh = view.findViewById(R.id.swipeRefresh)
        progressBar = view.findViewById(R.id.progressBar)
        tvEmpty = view.findViewById(R.id.tvEmpty)
        fabNuevaCompra = view.findViewById(R.id.fabNuevaCompra)
    }

    private fun setupRecyclerView() {
        adapter = ComprasAdapter { compra ->
            // Navegar a detalle
            val fragment = DetalleCompraFragment.newInstance(compra.id_compra)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun setupListeners() {
        swipeRefresh.setOnRefreshListener {
            loadCompras()
        }

        fabNuevaCompra.setOnClickListener {
            val fragment = CrearCompraFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun observeViewModel() {
        viewModel.comprasState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ComprasState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                    tvEmpty.visibility = View.GONE
                }
                is ComprasState.Success -> {
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    tvEmpty.visibility = View.GONE
                    adapter.submitList(state.compras)
                    swipeRefresh.isRefreshing = false
                }
                is ComprasState.Empty -> {
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                    tvEmpty.visibility = View.VISIBLE
                    swipeRefresh.isRefreshing = false
                }
                is ComprasState.Error -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    swipeRefresh.isRefreshing = false
                }
            }
        }
    }

    private fun loadCompras() {
        val sucursales = sessionManager.getUserSucursales()
        if (sucursales.isNotEmpty()) {
            val sucursalId = sucursales[0].id_sucursal
            viewModel.loadCompras(sucursalId)
        } else {
            Toast.makeText(requireContext(), "No tienes sucursales asignadas", Toast.LENGTH_LONG).show()
        }
    }
}
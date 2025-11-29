package com.intellisoft.androidcuisine.views.marketing

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
import com.intellisoft.androidcuisine.views.adapters.ClientesFrecuentesAdapter

class ClientesFrecuentesFragment : Fragment() {

    private lateinit var viewModel: MarketingViewModel

    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView
    private lateinit var fabAsignarCampania: FloatingActionButton

    private lateinit var adapter: ClientesFrecuentesAdapter
    private val clientesSeleccionados = mutableSetOf<Int>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_clientes_vip, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[MarketingViewModel::class.java]

        setupViews(view)
        setupRecyclerView()
        setupListeners()
        observeViewModel()

        view.findViewById<TextView>(R.id.tvTitle).text = "Clientes Frecuentes"
        viewModel.loadClientesFrecuentes(20)
    }

    private fun setupViews(view: View) {
        recyclerView = view.findViewById(R.id.recyclerViewClientes)
        swipeRefresh = view.findViewById(R.id.swipeRefresh)
        progressBar = view.findViewById(R.id.progressBar)
        tvEmpty = view.findViewById(R.id.tvEmpty)
        fabAsignarCampania = view.findViewById(R.id.fabAsignarCampania)
    }

    private fun setupRecyclerView() {
        adapter = ClientesFrecuentesAdapter(clientesSeleccionados)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun setupListeners() {
        swipeRefresh.setOnRefreshListener {
            viewModel.loadClientesFrecuentes(20)
        }

        fabAsignarCampania.setOnClickListener {
            if (clientesSeleccionados.isEmpty()) {
                Toast.makeText(requireContext(), "Selecciona al menos un cliente", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            showAsignarCampaniaDialog("frecuentes")
        }
    }

    private fun observeViewModel() {
        viewModel.clientesFrecuentesState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ClientesFrecuentesState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                    tvEmpty.visibility = View.GONE
                }
                is ClientesFrecuentesState.Success -> {
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    tvEmpty.visibility = View.GONE
                    adapter.submitList(state.clientes)
                    swipeRefresh.isRefreshing = false
                }
                is ClientesFrecuentesState.Empty -> {
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                    tvEmpty.visibility = View.VISIBLE
                    tvEmpty.text = "No hay clientes frecuentes"
                    swipeRefresh.isRefreshing = false
                }
                is ClientesFrecuentesState.Error -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    swipeRefresh.isRefreshing = false
                }
            }
        }
    }

    private fun showAsignarCampaniaDialog(tipoMetrica: String) {
        val dialog = AsignarCampaniaDialog.newInstance(
            tipoMetrica = tipoMetrica,
            clientesIds = clientesSeleccionados.toList()
        )
        dialog.show(parentFragmentManager, "AsignarCampaniaDialog")
    }
}
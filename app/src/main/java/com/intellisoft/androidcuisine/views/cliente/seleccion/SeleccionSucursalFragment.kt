package com.intellisoft.androidcuisine.views.cliente.seleccion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.data.remote.dto.SucursalActivaDto
import com.intellisoft.androidcuisine.views.adapters.SucursalesClienteAdapter
import com.intellisoft.androidcuisine.views.cliente.ClienteViewModel
import com.intellisoft.androidcuisine.views.cliente.SucursalesState

class SeleccionSucursalFragment : Fragment() {

    private lateinit var viewModel: ClienteViewModel

    private lateinit var tvTitulo: TextView
    private lateinit var tvSubtitulo: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var layoutEmpty: LinearLayout
    private lateinit var layoutError: LinearLayout
    private lateinit var tvError: TextView

    private lateinit var adapter: SucursalesClienteAdapter

    var onSucursalSeleccionada: ((SucursalActivaDto) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_seleccion_sucursal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[ClienteViewModel::class.java]

        initViews(view)
        setupRecyclerView()
        observeViewModel()

        viewModel.cargarSucursales()
    }

    private fun initViews(view: View) {
        tvTitulo = view.findViewById(R.id.tvTitulo)
        tvSubtitulo = view.findViewById(R.id.tvSubtitulo)
        recyclerView = view.findViewById(R.id.recyclerView)
        progressBar = view.findViewById(R.id.progressBar)
        layoutEmpty = view.findViewById(R.id.layoutEmpty)
        layoutError = view.findViewById(R.id.layoutError)
        tvError = view.findViewById(R.id.tvError)

        tvTitulo.text = "Selecciona Sucursal"
        tvSubtitulo.text = "Elige el restaurante donde deseas reservar"
    }

    private fun setupRecyclerView() {
        adapter = SucursalesClienteAdapter { sucursal ->
            viewModel.seleccionarSucursal(sucursal)
            viewModel.cargarAreas(sucursal.id_sucursal)
            onSucursalSeleccionada?.invoke(sucursal)
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.sucursalesState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is SucursalesState.Idle -> hideAll()
                is SucursalesState.Loading -> showLoading()
                is SucursalesState.Empty -> showEmpty()
                is SucursalesState.Success -> showContent(state.sucursales)
                is SucursalesState.Error -> showError(state.message)
            }
        }
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

    private fun showContent(sucursales: List<SucursalActivaDto>) {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        layoutEmpty.visibility = View.GONE
        layoutError.visibility = View.GONE
        adapter.submitList(sucursales)
    }

    private fun showError(message: String) {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.GONE
        layoutEmpty.visibility = View.GONE
        layoutError.visibility = View.VISIBLE
        tvError.text = message
    }
}
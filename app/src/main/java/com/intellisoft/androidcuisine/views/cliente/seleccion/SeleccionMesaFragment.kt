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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.data.remote.dto.MesaClienteDto
import com.intellisoft.androidcuisine.views.cliente.ClienteViewModel
import com.intellisoft.androidcuisine.views.cliente.MesasState
import com.intellisoft.androidcuisine.views.adapters.MesasAdapter

class SeleccionMesaFragment : Fragment() {

    private lateinit var viewModel: ClienteViewModel

    private lateinit var tvTitulo: TextView
    private lateinit var tvSubtitulo: TextView
    private lateinit var tvSucursalSeleccionada: TextView
    private lateinit var tvAreaSeleccionada: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var layoutEmpty: LinearLayout
    private lateinit var layoutError: LinearLayout
    private lateinit var tvError: TextView

    private lateinit var adapter: MesasAdapter

    var onMesaSeleccionada: ((MesaClienteDto) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_seleccion_mesa, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[ClienteViewModel::class.java]

        initViews(view)
        setupRecyclerView()
        observeViewModel()
    }

    private fun initViews(view: View) {
        tvTitulo = view.findViewById(R.id.tvTitulo)
        tvSubtitulo = view.findViewById(R.id.tvSubtitulo)
        tvSucursalSeleccionada = view.findViewById(R.id.tvSucursalSeleccionada)
        tvAreaSeleccionada = view.findViewById(R.id.tvAreaSeleccionada)
        recyclerView = view.findViewById(R.id.recyclerView)
        progressBar = view.findViewById(R.id.progressBar)
        layoutEmpty = view.findViewById(R.id.layoutEmpty)
        layoutError = view.findViewById(R.id.layoutError)
        tvError = view.findViewById(R.id.tvError)

        tvTitulo.text = "Selecciona Mesa"
        tvSubtitulo.text = "Elige la mesa de tu preferencia"
    }

    private fun setupRecyclerView() {
        adapter = MesasAdapter { mesa ->
            if (mesa.estatus_actual == 1) { // Solo disponibles
                viewModel.seleccionarMesa(mesa)
                onMesaSeleccionada?.invoke(mesa)
            }
        }

        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.sucursalSeleccionada.observe(viewLifecycleOwner) { sucursal ->
            sucursal?.let {
                tvSucursalSeleccionada.text = "📍 ${it.nombre}"
                tvSucursalSeleccionada.visibility = View.VISIBLE
            }
        }

        viewModel.areaSeleccionada.observe(viewLifecycleOwner) { area ->
            area?.let {
                tvAreaSeleccionada.text = "🏠 ${it.nombre}"
                tvAreaSeleccionada.visibility = View.VISIBLE
            }
        }

        viewModel.mesasState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is MesasState.Idle -> hideAll()
                is MesasState.Loading -> showLoading()
                is MesasState.Empty -> showEmpty()
                is MesasState.Success -> showContent(state.mesas)
                is MesasState.Error -> showError(state.message)
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

    private fun showContent(mesas: List<MesaClienteDto>) {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        layoutEmpty.visibility = View.GONE
        layoutError.visibility = View.GONE
        adapter.submitList(mesas)
    }

    private fun showError(message: String) {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.GONE
        layoutEmpty.visibility = View.GONE
        layoutError.visibility = View.VISIBLE
        tvError.text = message
    }
}
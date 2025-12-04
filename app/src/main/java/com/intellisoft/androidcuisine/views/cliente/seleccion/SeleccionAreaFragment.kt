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
import com.intellisoft.androidcuisine.data.remote.dto.AreaClienteDto
import com.intellisoft.androidcuisine.views.adapters.AreasAdapter
import com.intellisoft.androidcuisine.views.cliente.ClienteViewModel
import com.intellisoft.androidcuisine.views.cliente.AreasState

class SeleccionAreaFragment : Fragment() {

    private lateinit var viewModel: ClienteViewModel

    private lateinit var tvTitulo: TextView
    private lateinit var tvSubtitulo: TextView
    private lateinit var tvSucursalSeleccionada: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var layoutEmpty: LinearLayout
    private lateinit var layoutError: LinearLayout
    private lateinit var tvError: TextView

    private lateinit var adapter: AreasAdapter

    var onAreaSeleccionada: ((AreaClienteDto) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_seleccion_area, container, false)
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
        recyclerView = view.findViewById(R.id.recyclerView)
        progressBar = view.findViewById(R.id.progressBar)
        layoutEmpty = view.findViewById(R.id.layoutEmpty)
        layoutError = view.findViewById(R.id.layoutError)
        tvError = view.findViewById(R.id.tvError)

        tvTitulo.text = "Selecciona Área"
        tvSubtitulo.text = "Elige el área donde deseas sentarte"
    }

    private fun setupRecyclerView() {
        adapter = AreasAdapter { area ->
            viewModel.seleccionarArea(area)
            onAreaSeleccionada?.invoke(area)
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.sucursalSeleccionada.observe(viewLifecycleOwner) { sucursal ->
            sucursal?.let {
                tvSucursalSeleccionada.text = "📍 ${it.nombre}"
                tvSucursalSeleccionada.visibility = View.VISIBLE
            }
        }

        viewModel.areasState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AreasState.Idle -> hideAll()
                is AreasState.Loading -> showLoading()
                is AreasState.Empty -> showEmpty()
                is AreasState.Success -> showContent(state.areas)
                is AreasState.Error -> showError(state.message)
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

    private fun showContent(areas: List<AreaClienteDto>) {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        layoutEmpty.visibility = View.GONE
        layoutError.visibility = View.GONE
        adapter.submitList(areas)
    }

    private fun showError(message: String) {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.GONE
        layoutEmpty.visibility = View.GONE
        layoutError.visibility = View.VISIBLE
        tvError.text = message
    }
}
package com.intellisoft.androidcuisine.views.cliente.cupones

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
import com.intellisoft.androidcuisine.data.remote.dto.CuponClienteDto
import com.intellisoft.androidcuisine.views.adapters.CuponesAdapter
import com.intellisoft.androidcuisine.views.cliente.ClienteViewModel
import com.intellisoft.androidcuisine.views.cliente.MisCuponesState

class MisCuponesFragment : Fragment() {

    private lateinit var viewModel: ClienteViewModel

    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var chipGroup: ChipGroup
    private lateinit var chipVigentes: Chip
    private lateinit var chipTodos: Chip
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var layoutEmpty: LinearLayout
    private lateinit var layoutError: LinearLayout

    private lateinit var adapter: CuponesAdapter

    private var soloVigentes: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mis_cupones, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[ClienteViewModel::class.java]

        initViews(view)
        setupRecyclerView()
        setupChipFilters()
        setupSwipeRefresh()
        observeViewModel()

        cargarCupones()
    }

    private fun initViews(view: View) {
        swipeRefresh = view.findViewById(R.id.swipeRefresh)
        chipGroup = view.findViewById(R.id.chipGroup)
        chipVigentes = view.findViewById(R.id.chipVigentes)
        chipTodos = view.findViewById(R.id.chipTodos)
        recyclerView = view.findViewById(R.id.recyclerView)
        progressBar = view.findViewById(R.id.progressBar)
        layoutEmpty = view.findViewById(R.id.layoutEmpty)
        layoutError = view.findViewById(R.id.layoutError)
    }

    private fun setupRecyclerView() {
        adapter = CuponesAdapter { cupon ->
            mostrarDetalleCupon(cupon)
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun setupChipFilters() {
        chipVigentes.setOnClickListener {
            soloVigentes = true
            cargarCupones()
        }

        chipTodos.setOnClickListener {
            soloVigentes = false
            cargarCupones()
        }
    }

    private fun setupSwipeRefresh() {
        swipeRefresh.setColorSchemeResources(R.color.cuisine_purple)
        swipeRefresh.setOnRefreshListener {
            cargarCupones()
        }
    }

    private fun observeViewModel() {
        viewModel.misCuponesState.observe(viewLifecycleOwner) { state ->
            swipeRefresh.isRefreshing = false

            when (state) {
                is MisCuponesState.Idle -> hideAll()
                is MisCuponesState.Loading -> showLoading()
                is MisCuponesState.Empty -> showEmpty()
                is MisCuponesState.Success -> showContent(state.cupones)
                is MisCuponesState.Error -> showError(state.message)
            }
        }
    }

    private fun cargarCupones() {
        viewModel.cargarMisCupones(soloVigentes)
    }

    private fun mostrarDetalleCupon(cupon: CuponClienteDto) {
        val mensaje = buildString {
            append("🎟️ ${cupon.codigo}\n\n")
            append("Descuento: ${cupon.porcentaje_desc}%\n")
            cupon.nombre_campania?.let { append("Descripción: $it\n") }
            cupon.fecha_vigencia?.let { append("Expira: ${it.take(10)}\n") }
        }

        com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
            .setTitle("Detalle del Cupón")
            .setMessage(mensaje)
            .setPositiveButton("Cerrar", null)
            .show()
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

    private fun showContent(cupones: List<CuponClienteDto>) {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        layoutEmpty.visibility = View.GONE
        layoutError.visibility = View.GONE
        adapter.submitList(cupones)
    }

    private fun showError(message: String) {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.GONE
        layoutEmpty.visibility = View.GONE
        layoutError.visibility = View.VISIBLE
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
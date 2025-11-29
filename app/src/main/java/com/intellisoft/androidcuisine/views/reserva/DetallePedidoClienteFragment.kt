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
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.textview.MaterialTextView
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.data.remote.dto.PedidoDetalleDto
import com.intellisoft.androidcuisine.views.adapters.ItemsPedidoAdapter
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class DetallePedidoClienteFragment : Fragment() {

    companion object {
        private const val ARG_PEDIDO_ID = "pedido_id"

        fun newInstance(pedidoId: Int): DetallePedidoClienteFragment {
            return DetallePedidoClienteFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PEDIDO_ID, pedidoId)
                }
            }
        }
    }

    private lateinit var viewModel: PedidosClienteViewModel

    private lateinit var tvPedidoId: MaterialTextView
    private lateinit var tvFecha: MaterialTextView
    private lateinit var chipTipo: Chip
    private lateinit var chipEstado: Chip
    private lateinit var recyclerItems: RecyclerView
    private lateinit var tvTotal: MaterialTextView
    private lateinit var progressBar: ProgressBar
    private lateinit var layoutContent: LinearLayout
    private lateinit var btnVolver: MaterialButton

    private lateinit var adapter: ItemsPedidoAdapter

    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
    private var pedidoId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detalle_pedido_cliente, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pedidoId = arguments?.getInt(ARG_PEDIDO_ID) ?: 0

        viewModel = ViewModelProvider(requireActivity())[PedidosClienteViewModel::class.java]

        setupViews(view)
        setupRecyclerView()
        setupListeners()
        observeViewModel()

        viewModel.getPedidoDetalle(pedidoId)
    }

    private fun setupViews(view: View) {
        tvPedidoId = view.findViewById(R.id.tvPedidoId)
        tvFecha = view.findViewById(R.id.tvFecha)
        chipTipo = view.findViewById(R.id.chipTipo)
        chipEstado = view.findViewById(R.id.chipEstado)
        recyclerItems = view.findViewById(R.id.recyclerItems)
        tvTotal = view.findViewById(R.id.tvTotal)
        progressBar = view.findViewById(R.id.progressBar)
        layoutContent = view.findViewById(R.id.layoutContent)
        btnVolver = view.findViewById(R.id.btnVolver)
    }

    private fun setupRecyclerView() {
        adapter = ItemsPedidoAdapter()
        recyclerItems.layoutManager = LinearLayoutManager(requireContext())
        recyclerItems.adapter = adapter
    }

    private fun setupListeners() {
        btnVolver.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun observeViewModel() {
        viewModel.pedidoDetalleState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PedidoDetalleState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    layoutContent.visibility = View.GONE
                }
                is PedidoDetalleState.Success -> {
                    progressBar.visibility = View.GONE
                    layoutContent.visibility = View.VISIBLE
                    mostrarPedido(state.pedido)
                }
                is PedidoDetalleState.Error -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    parentFragmentManager.popBackStack()
                }
                else -> {}
            }
        }
    }

    private fun mostrarPedido(pedido: PedidoDetalleDto) {
        tvPedidoId.text = "Pedido #${pedido.id_pedido}"
        tvFecha.text = formatearFecha(pedido.created_at ?: "")

        // Tipo
        chipTipo.text = pedido.tipo_descripcion ?: if (pedido.tipo_pedido == 1) "En sitio" else "Para llevar"

        // Estado
        chipEstado.text = pedido.estado_descripcion ?: getEstadoTexto(pedido.estado)
        configurarChipEstado(pedido.estado)

        // Items
        adapter.submitList(pedido.items ?: emptyList())

        // Total
        tvTotal.text = currencyFormat.format(pedido.total ?: 0.0)
    }

    private fun configurarChipEstado(estado: Int) {
        when (estado) {
            0 -> {
                chipEstado.setChipBackgroundColorResource(R.color.cuisine_green_dark)
                chipEstado.setTextColor(requireContext().getColor(R.color.white))
            }
            3 -> {
                chipEstado.setChipBackgroundColorResource(android.R.color.holo_blue_light)
                chipEstado.setTextColor(requireContext().getColor(R.color.white))
            }
            4 -> {
                chipEstado.setChipBackgroundColorResource(android.R.color.holo_red_light)
                chipEstado.setTextColor(requireContext().getColor(R.color.white))
            }
            5 -> {
                chipEstado.setChipBackgroundColorResource(android.R.color.darker_gray)
                chipEstado.setTextColor(requireContext().getColor(R.color.white))
            }
        }
    }

    private fun getEstadoTexto(estado: Int): String {
        return when (estado) {
            0 -> "En preparación"
            3 -> "Completo"
            4 -> "Cancelado"
            5 -> "Pagado"
            else -> "Desconocido"
        }
    }

    private fun formatearFecha(fechaHora: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("EEEE, dd MMMM yyyy • HH:mm", Locale("es", "MX"))
            val date = inputFormat.parse(fechaHora)
            if (date != null) outputFormat.format(date) else fechaHora
        } catch (e: Exception) {
            fechaHora
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.resetPedidoDetalleState()
    }
}
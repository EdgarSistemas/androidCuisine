package com.intellisoft.androidcuisine.views.cliente.pedidos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.data.remote.dto.PedidoClienteDto
import com.intellisoft.androidcuisine.views.adapters.ItemsPedidoAdapter
import com.intellisoft.androidcuisine.views.cliente.ClienteViewModel
import com.intellisoft.androidcuisine.views.cliente.PedidoDetalleState

class DetallePedidoFragment : Fragment() {

    private lateinit var viewModel: ClienteViewModel

    private lateinit var tvIdPedido: TextView
    private lateinit var chipEstatus: Chip
    private lateinit var tvTipo: TextView
    private lateinit var tvFecha: TextView
    private lateinit var recyclerViewItems: RecyclerView
    private lateinit var tvSubtotal: TextView
    private lateinit var tvDescuento: TextView
    private lateinit var layoutDescuento: LinearLayout
    private lateinit var tvTotal: TextView
    private lateinit var btnCancelar: MaterialButton
    private lateinit var progressBar: ProgressBar

    private lateinit var itemsAdapter: ItemsPedidoAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detalle_pedido_cliente, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[ClienteViewModel::class.java]

        initViews(view)
        setupRecyclerView()
        observeViewModel()
    }

    private fun initViews(view: View) {
        tvIdPedido = view.findViewById(R.id.tvIdPedido)
        chipEstatus = view.findViewById(R.id.chipEstatus)
        tvTipo = view.findViewById(R.id.tvTipo)
        tvFecha = view.findViewById(R.id.tvFecha)
        recyclerViewItems = view.findViewById(R.id.recyclerViewItems)
        tvSubtotal = view.findViewById(R.id.tvSubtotal)
        tvDescuento = view.findViewById(R.id.tvDescuento)
        layoutDescuento = view.findViewById(R.id.layoutDescuento)
        tvTotal = view.findViewById(R.id.tvTotal)
        btnCancelar = view.findViewById(R.id.btnCancelar)
        progressBar = view.findViewById(R.id.progressBar)
    }

    private fun setupRecyclerView() {
        itemsAdapter = ItemsPedidoAdapter()
        recyclerViewItems.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewItems.adapter = itemsAdapter
        recyclerViewItems.isNestedScrollingEnabled = false
    }

    private fun observeViewModel() {
        viewModel.pedidoDetalleState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PedidoDetalleState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                }
                is PedidoDetalleState.Success -> {
                    progressBar.visibility = View.GONE
                    mostrarDetalle(state.pedido)
                }
                is PedidoDetalleState.Error -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }

    private fun mostrarDetalle(pedido: PedidoClienteDto) {
        tvIdPedido.text = "Pedido #${pedido.id_pedido}"

        chipEstatus.text = pedido.estado_display ?: "Pendiente"

        val tipoTexto = when (pedido.tipo_pedido) {
            1 -> "🍽️ Comer aquí"
            2 -> "🥡 Para llevar"
            else -> "Pedido"
        }
        tvTipo.text = tipoTexto

        tvFecha.text = "📅 ${pedido.created_at?.take(16) ?: ""}"

        // Items
        pedido.items?.let { items ->
            itemsAdapter.submitList(items)
        }

        // Totales
        val subtotal = pedido.total ?: pedido.total ?: 0.0
        tvSubtotal.text = "$${String.format("%.2f", subtotal)}"

        tvTotal.text = "$${String.format("%.2f", pedido.total ?: 0.0)}"

        // Botón cancelar solo si está pendiente
        if (pedido.estado_pedido == 1) {
            btnCancelar.visibility = View.VISIBLE
            btnCancelar.setOnClickListener {
                viewModel.cancelarPedido(pedido.id_pedido)
                parentFragmentManager.popBackStack()
            }
        } else {
            btnCancelar.visibility = View.GONE
        }
    }
}
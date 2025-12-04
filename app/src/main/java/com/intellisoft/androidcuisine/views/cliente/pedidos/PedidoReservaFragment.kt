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
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.data.remote.dto.ComboDto
import com.intellisoft.androidcuisine.data.remote.dto.ItemPedidoRequest
import com.intellisoft.androidcuisine.data.remote.dto.ProductoDto
import com.intellisoft.androidcuisine.views.adapters.CombosMenuAdapter
import com.intellisoft.androidcuisine.views.adapters.ProductosMenuAdapter
import com.intellisoft.androidcuisine.views.cliente.ClienteViewModel
import com.intellisoft.androidcuisine.views.cliente.CombosState
import com.intellisoft.androidcuisine.views.cliente.CrearPedidoState
import com.intellisoft.androidcuisine.views.cliente.ProductosState

class PedidoReservaFragment : Fragment() {

    companion object {
        private const val ARG_RESERVA_ID = "reserva_id"
        private const val ARG_SUCURSAL_ID = "sucursal_id"

        fun newInstance(reservaId: Int, sucursalId: Int): PedidoReservaFragment {
            return PedidoReservaFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_RESERVA_ID, reservaId)
                    putInt(ARG_SUCURSAL_ID, sucursalId)
                }
            }
        }
    }

    private var reservaId: Int = 0
    private var sucursalId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        reservaId = arguments?.getInt(ARG_RESERVA_ID) ?: 0
        sucursalId = arguments?.getInt(ARG_SUCURSAL_ID) ?: 0
    }

    private lateinit var viewModel: ClienteViewModel

    private lateinit var tvTitulo: TextView
    private lateinit var chipGroup: ChipGroup
    private lateinit var chipProductos: Chip
    private lateinit var chipCombos: Chip
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var layoutEmpty: LinearLayout

    // Carrito
    private lateinit var cardCarrito: MaterialCardView
    private lateinit var tvItemsCarrito: TextView
    private lateinit var tvTotalCarrito: TextView
    private lateinit var btnVerCarrito: MaterialButton

    private lateinit var productosAdapter: ProductosMenuAdapter
    private lateinit var combosAdapter: CombosMenuAdapter

    private val carrito = mutableListOf<CarritoItem>()

    data class CarritoItem(
        val productoId: Int?,
        val comboId: Int?,
        val nombre: String,
        val precio: Double,
        var cantidad: Int
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pedido_reserva, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[ClienteViewModel::class.java]

        initViews(view)
        setupAdapters()
        setupChipFilters()
        setupCarrito()
        observeViewModel()

        // Cargar productos por defecto
        viewModel.cargarProductos()
    }

    private fun initViews(view: View) {
        tvTitulo = view.findViewById(R.id.tvTitulo)
        chipGroup = view.findViewById(R.id.chipGroup)
        chipProductos = view.findViewById(R.id.chipProductos)
        chipCombos = view.findViewById(R.id.chipCombos)
        recyclerView = view.findViewById(R.id.recyclerView)
        progressBar = view.findViewById(R.id.progressBar)
        layoutEmpty = view.findViewById(R.id.layoutEmpty)

        cardCarrito = view.findViewById(R.id.cardCarrito)
        tvItemsCarrito = view.findViewById(R.id.tvItemsCarrito)
        tvTotalCarrito = view.findViewById(R.id.tvTotalCarrito)
        btnVerCarrito = view.findViewById(R.id.btnVerCarrito)

        tvTitulo.text = "🍽️ Ordenar - Reserva #$reservaId"
    }

    private fun setupAdapters() {
        productosAdapter = ProductosMenuAdapter { producto ->
            agregarAlCarrito(producto)
        }

        combosAdapter = CombosMenuAdapter { combo ->
            agregarComboAlCarrito(combo)
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = productosAdapter
    }

    private fun setupChipFilters() {
        chipProductos.setOnClickListener {
            recyclerView.adapter = productosAdapter
            viewModel.cargarProductos()
        }

        chipCombos.setOnClickListener {
            recyclerView.adapter = combosAdapter
            viewModel.cargarCombos()
        }
    }

    private fun setupCarrito() {
        cardCarrito.visibility = View.GONE

        btnVerCarrito.setOnClickListener {
            mostrarDialogoCarrito()
        }
    }

    private fun observeViewModel() {
        viewModel.productosState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ProductosState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                    layoutEmpty.visibility = View.GONE
                }
                is ProductosState.Success -> {
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    layoutEmpty.visibility = View.GONE
                    productosAdapter.submitList(state.productos)
                }
                is ProductosState.Empty -> {
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                    layoutEmpty.visibility = View.VISIBLE
                }
                is ProductosState.Error -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }

        viewModel.combosState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is CombosState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                    layoutEmpty.visibility = View.GONE
                }
                is CombosState.Success -> {
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    layoutEmpty.visibility = View.GONE
                    combosAdapter.submitList(state.combos)
                }
                is CombosState.Empty -> {
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                    layoutEmpty.visibility = View.VISIBLE
                }
                is CombosState.Error -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }

        viewModel.crearPedidoState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is CrearPedidoState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    btnVerCarrito.isEnabled = false
                }
                is CrearPedidoState.Success -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "✅ ¡Pedido creado exitosamente!", Toast.LENGTH_LONG).show()
                    viewModel.resetCrearPedidoState()
                    carrito.clear()
                    actualizarCarritoUI()
                    // Volver atrás
                    requireActivity().supportFragmentManager.popBackStack()
                }
                is CrearPedidoState.Error -> {
                    progressBar.visibility = View.GONE
                    btnVerCarrito.isEnabled = true
                    Toast.makeText(requireContext(), "❌ ${state.message}", Toast.LENGTH_LONG).show()
                    viewModel.resetCrearPedidoState()
                }
                else -> {
                    progressBar.visibility = View.GONE
                    btnVerCarrito.isEnabled = true
                }
            }
        }
    }

    private fun agregarAlCarrito(producto: ProductoDto) {
        val existente = carrito.find { it.productoId == producto.id_producto }
        if (existente != null) {
            existente.cantidad++
        } else {
            carrito.add(
                CarritoItem(
                    productoId = producto.id_producto,
                    comboId = null,
                    nombre = producto.nombre,
                    precio = producto.precio,
                    cantidad = 1
                )
            )
        }
        actualizarCarritoUI()
        Toast.makeText(requireContext(), "➕ ${producto.nombre} agregado", Toast.LENGTH_SHORT).show()
    }

    private fun agregarComboAlCarrito(combo: ComboDto) {
        val existente = carrito.find { it.comboId == combo.id_combo }
        if (existente != null) {
            existente.cantidad++
        } else {
            carrito.add(
                CarritoItem(
                    productoId = null,
                    comboId = combo.id_combo,
                    nombre = combo.nombre,
                    precio = combo.precio,
                    cantidad = 1
                )
            )
        }
        actualizarCarritoUI()
        Toast.makeText(requireContext(), "➕ ${combo.nombre} agregado", Toast.LENGTH_SHORT).show()
    }

    private fun actualizarCarritoUI() {
        if (carrito.isEmpty()) {
            cardCarrito.visibility = View.GONE
        } else {
            cardCarrito.visibility = View.VISIBLE
            val totalItems = carrito.sumOf { it.cantidad }
            val totalPrecio = carrito.sumOf { it.precio * it.cantidad }
            tvItemsCarrito.text = "$totalItems items"
            tvTotalCarrito.text = "$${String.format("%.2f", totalPrecio)}"
        }
    }

    private fun mostrarDialogoCarrito() {
        if (carrito.isEmpty()) {
            Toast.makeText(requireContext(), "El carrito está vacío", Toast.LENGTH_SHORT).show()
            return
        }

        val resumen = StringBuilder()
        resumen.append("📋 Resumen del pedido:\n\n")

        var total = 0.0
        carrito.forEach { item ->
            val subtotal = item.precio * item.cantidad
            total += subtotal
            resumen.append("${item.cantidad}x ${item.nombre} - $${String.format("%.2f", subtotal)}\n")
        }

        resumen.append("\n─────────────────\n")
        resumen.append("💰 Total: $${String.format("%.2f", total)}")

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("🛒 Tu Carrito")
            .setMessage(resumen.toString())
            .setPositiveButton("Confirmar Pedido") { _, _ ->
                confirmarPedido()
            }
            .setNegativeButton("Seguir ordenando", null)
            .setNeutralButton("Vaciar carrito") { _, _ ->
                carrito.clear()
                actualizarCarritoUI()
            }
            .show()
    }

    private fun confirmarPedido() {
        if (carrito.isEmpty()) {
            Toast.makeText(requireContext(), "El carrito está vacío", Toast.LENGTH_SHORT).show()
            return
        }

        val items = carrito.map { item ->
            ItemPedidoRequest(
                producto_id = item.productoId,
                combo_id = item.comboId,
                cantidad = item.cantidad,
                notas = null
            )
        }

        viewModel.crearPedidoDineIn(
            reservaId = reservaId,
            sucursalId = sucursalId,  // ← PASAR sucursalId
            notas = null,
            items = items
        )
    }
}
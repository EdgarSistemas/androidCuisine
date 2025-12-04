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
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.data.remote.dto.ProductoDto
import com.intellisoft.androidcuisine.data.remote.dto.ComboDto
import com.intellisoft.androidcuisine.data.remote.dto.SucursalActivaDto
import com.intellisoft.androidcuisine.views.adapters.CombosMenuAdapter
import com.intellisoft.androidcuisine.views.adapters.ProductosMenuAdapter
import com.intellisoft.androidcuisine.views.adapters.SucursalesTakeawayAdapter
import com.intellisoft.androidcuisine.views.cliente.ClienteViewModel
import com.intellisoft.androidcuisine.views.cliente.SucursalesState
import com.intellisoft.androidcuisine.views.cliente.ProductosState
import com.intellisoft.androidcuisine.views.cliente.CombosState
import com.intellisoft.androidcuisine.views.cliente.CrearPedidoState

class PedidoTakeawayFragment : Fragment() {

    private lateinit var viewModel: ClienteViewModel

    // Views - Selección Sucursal
    private lateinit var cardSeleccionSucursal: MaterialCardView
    private lateinit var tvSucursalSeleccionada: TextView
    private lateinit var btnCambiarSucursal: MaterialButton
    private lateinit var layoutSeleccionarSucursal: LinearLayout

    // Views - Menú
    private lateinit var layoutMenu: LinearLayout
    private lateinit var chipGroupCategorias: ChipGroup
    private lateinit var chipProductos: Chip
    private lateinit var chipCombos: Chip
    private lateinit var recyclerViewMenu: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var layoutEmpty: LinearLayout

    // Views - Carrito
    private lateinit var cardCarrito: MaterialCardView
    private lateinit var tvItemsCarrito: TextView
    private lateinit var tvTotalCarrito: TextView
    private lateinit var btnVerCarrito: MaterialButton

    // Views - Lista Sucursales
    private lateinit var layoutListaSucursales: LinearLayout
    private lateinit var recyclerViewSucursales: RecyclerView
    private lateinit var progressSucursales: ProgressBar

    private lateinit var productosAdapter: ProductosMenuAdapter
    private lateinit var combosAdapter: CombosMenuAdapter
    private lateinit var sucursalesAdapter: SucursalesTakeawayAdapter

    // Datos
    private var sucursalSeleccionada: SucursalActivaDto? = null
    private val carrito = mutableListOf<CarritoItem>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pedido_takeaway, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[ClienteViewModel::class.java]

        initViews(view)
        setupAdapters()
        setupListeners()
        observeViewModel()

        // Mostrar selección de sucursal primero
        mostrarSeleccionSucursal()
    }

    private fun initViews(view: View) {
        // Selección Sucursal
        cardSeleccionSucursal = view.findViewById(R.id.cardSeleccionSucursal)
        tvSucursalSeleccionada = view.findViewById(R.id.tvSucursalSeleccionada)
        btnCambiarSucursal = view.findViewById(R.id.btnCambiarSucursal)
        layoutSeleccionarSucursal = view.findViewById(R.id.layoutSeleccionarSucursal)

        // Lista Sucursales
        layoutListaSucursales = view.findViewById(R.id.layoutListaSucursales)
        recyclerViewSucursales = view.findViewById(R.id.recyclerViewSucursales)
        progressSucursales = view.findViewById(R.id.progressSucursales)

        // Menú
        layoutMenu = view.findViewById(R.id.layoutMenu)
        chipGroupCategorias = view.findViewById(R.id.chipGroupCategorias)
        chipProductos = view.findViewById(R.id.chipProductos)
        chipCombos = view.findViewById(R.id.chipCombos)
        recyclerViewMenu = view.findViewById(R.id.recyclerViewMenu)
        progressBar = view.findViewById(R.id.progressBar)
        layoutEmpty = view.findViewById(R.id.layoutEmpty)

        // Carrito
        cardCarrito = view.findViewById(R.id.cardCarrito)
        tvItemsCarrito = view.findViewById(R.id.tvItemsCarrito)
        tvTotalCarrito = view.findViewById(R.id.tvTotalCarrito)
        btnVerCarrito = view.findViewById(R.id.btnVerCarrito)
    }

    private fun setupAdapters() {
        // Adapter sucursales
        sucursalesAdapter = SucursalesTakeawayAdapter { sucursal ->
            sucursalSeleccionada = sucursal
            mostrarMenu()
        }
        recyclerViewSucursales.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewSucursales.adapter = sucursalesAdapter

        // Adapter productos
        productosAdapter = ProductosMenuAdapter { producto ->
            agregarAlCarrito(producto)
        }

        // Adapter combos
        combosAdapter = CombosMenuAdapter { combo ->
            agregarComboAlCarrito(combo)
        }

        recyclerViewMenu.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewMenu.adapter = productosAdapter
    }

    private fun setupListeners() {
        // Cambiar sucursal
        btnCambiarSucursal.setOnClickListener {
            mostrarSeleccionSucursal()
        }

        layoutSeleccionarSucursal.setOnClickListener {
            mostrarSeleccionSucursal()
        }

        // Filtros productos/combos
        chipProductos.setOnClickListener {
            recyclerViewMenu.adapter = productosAdapter
            viewModel.cargarProductos()
        }

        chipCombos.setOnClickListener {
            recyclerViewMenu.adapter = combosAdapter
            viewModel.cargarCombos()
        }

        // Carrito
        btnVerCarrito.setOnClickListener {
            if (carrito.isEmpty()) {
                Toast.makeText(requireContext(), "El carrito está vacío", Toast.LENGTH_SHORT).show()
            } else {
                mostrarDialogoCarrito()
            }
        }
    }

    private fun observeViewModel() {
        // Sucursales
        viewModel.sucursalesState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is SucursalesState.Loading -> {
                    progressSucursales.visibility = View.VISIBLE
                    recyclerViewSucursales.visibility = View.GONE
                }
                is SucursalesState.Success -> {
                    progressSucursales.visibility = View.GONE
                    recyclerViewSucursales.visibility = View.VISIBLE
                    sucursalesAdapter.submitList(state.sucursales)
                }
                is SucursalesState.Empty -> {
                    progressSucursales.visibility = View.GONE
                    Toast.makeText(requireContext(), "No hay sucursales disponibles", Toast.LENGTH_SHORT).show()
                }
                is SucursalesState.Error -> {
                    progressSucursales.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }

        // Productos
        viewModel.productosState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ProductosState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    recyclerViewMenu.visibility = View.GONE
                    layoutEmpty.visibility = View.GONE
                }
                is ProductosState.Success -> {
                    progressBar.visibility = View.GONE
                    recyclerViewMenu.visibility = View.VISIBLE
                    layoutEmpty.visibility = View.GONE
                    productosAdapter.submitList(state.productos)
                }
                is ProductosState.Empty -> {
                    progressBar.visibility = View.GONE
                    recyclerViewMenu.visibility = View.GONE
                    layoutEmpty.visibility = View.VISIBLE
                }
                is ProductosState.Error -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }

        // Combos
        viewModel.combosState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is CombosState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    recyclerViewMenu.visibility = View.GONE
                    layoutEmpty.visibility = View.GONE
                }
                is CombosState.Success -> {
                    progressBar.visibility = View.GONE
                    recyclerViewMenu.visibility = View.VISIBLE
                    layoutEmpty.visibility = View.GONE
                    combosAdapter.submitList(state.combos)
                }
                is CombosState.Empty -> {
                    progressBar.visibility = View.GONE
                    recyclerViewMenu.visibility = View.GONE
                    layoutEmpty.visibility = View.VISIBLE
                }
                is CombosState.Error -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }

        // Crear Pedido
        viewModel.crearPedidoState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is CrearPedidoState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                }
                is CrearPedidoState.Success -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "¡Pedido creado exitosamente!", Toast.LENGTH_LONG).show()
                    carrito.clear()
                    actualizarCarritoUI()
                    viewModel.resetCrearPedidoState()
                }
                is CrearPedidoState.Error -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    viewModel.resetCrearPedidoState()
                }
                else -> {}
            }
        }
    }

    private fun mostrarSeleccionSucursal() {
        layoutListaSucursales.visibility = View.VISIBLE
        layoutMenu.visibility = View.GONE
        cardSeleccionSucursal.visibility = View.GONE
        cardCarrito.visibility = View.GONE

        viewModel.cargarSucursales()
    }

    private fun mostrarMenu() {
        layoutListaSucursales.visibility = View.GONE
        layoutMenu.visibility = View.VISIBLE
        cardSeleccionSucursal.visibility = View.VISIBLE

        tvSucursalSeleccionada.text = "📍 ${sucursalSeleccionada?.nombre ?: "Sucursal"}"

        // Cargar productos
        viewModel.cargarProductos()
        actualizarCarritoUI()
    }

    private fun agregarAlCarrito(producto: ProductoDto) {
        val existente = carrito.find { it.productoId == producto.id_producto && it.comboId == null }
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
        Toast.makeText(requireContext(), "${producto.nombre} agregado", Toast.LENGTH_SHORT).show()
    }

    private fun agregarComboAlCarrito(combo: ComboDto) {
        val existente = carrito.find { it.comboId == combo.id_combo && it.productoId == null }
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
        Toast.makeText(requireContext(), "${combo.nombre} agregado", Toast.LENGTH_SHORT).show()
    }

    private fun actualizarCarritoUI() {
        val totalItems = carrito.sumOf { it.cantidad }
        val totalPrecio = carrito.sumOf { it.precio * it.cantidad }

        if (totalItems > 0 && sucursalSeleccionada != null) {
            cardCarrito.visibility = View.VISIBLE
            tvItemsCarrito.text = "$totalItems items"
            tvTotalCarrito.text = "$${String.format("%.2f", totalPrecio)}"
        } else {
            cardCarrito.visibility = View.GONE
        }
    }

    private fun mostrarDialogoCarrito() {
        val items = carrito.map { "${it.cantidad}x ${it.nombre} - $${String.format("%.2f", it.precio * it.cantidad)}" }
        val total = carrito.sumOf { it.precio * it.cantidad }

        val mensaje = "📍 Recoger en: ${sucursalSeleccionada?.nombre}\n\n" +
                items.joinToString("\n") +
                "\n\nTotal: $${String.format("%.2f", total)}"

        com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
            .setTitle("Tu Pedido para Llevar")
            .setMessage(mensaje)
            .setPositiveButton("Confirmar Pedido") { _, _ ->
                confirmarPedido()
            }
            .setNegativeButton("Seguir comprando", null)
            .setNeutralButton("Vaciar") { _, _ ->
                carrito.clear()
                actualizarCarritoUI()
            }
            .show()
    }

    private fun confirmarPedido() {
        val sucursal = sucursalSeleccionada
        if (sucursal == null) {
            Toast.makeText(requireContext(), "Selecciona una sucursal", Toast.LENGTH_SHORT).show()
            return
        }

        if (carrito.isEmpty()) {
            Toast.makeText(requireContext(), "El carrito está vacío", Toast.LENGTH_SHORT).show()
            return
        }

        val items = carrito.map { item ->
            com.intellisoft.androidcuisine.data.remote.dto.ItemPedidoRequest(
                producto_id = item.productoId,
                combo_id = item.comboId,
                cantidad = item.cantidad,
                notas = null
            )
        }

        viewModel.crearPedidoTakeaway(
            sucursalId = sucursal.id_sucursal,
            items = items,
            notas = null
        )
    }
}

// Data class para el carrito local
data class CarritoItem(
    val productoId: Int?,
    val comboId: Int?,
    val nombre: String,
    val precio: Double,
    var cantidad: Int
)
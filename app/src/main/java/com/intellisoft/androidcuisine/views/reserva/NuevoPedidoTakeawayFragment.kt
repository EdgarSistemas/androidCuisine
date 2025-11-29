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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.data.remote.dto.ComboDto
import com.intellisoft.androidcuisine.data.remote.dto.ProductoDto
import com.intellisoft.androidcuisine.views.adapters.CarritoAdapter
import com.intellisoft.androidcuisine.views.adapters.CombosMenuAdapter
import com.intellisoft.androidcuisine.views.reserva.ReservasClienteViewModel
import java.text.NumberFormat
import java.util.*

class NuevoPedidoTakeawayFragment : Fragment() {

    private lateinit var viewModel: PedidosClienteViewModel

    private lateinit var tvSucursal: MaterialTextView
    private lateinit var chipGroupCategorias: ChipGroup
    private lateinit var chipCombos: Chip
    private lateinit var recyclerProductos: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var layoutEmpty: LinearLayout

    // Carrito bottom bar
    private lateinit var layoutCarrito: LinearLayout
    private lateinit var tvCantidadCarrito: MaterialTextView
    private lateinit var tvTotalCarrito: MaterialTextView
    private lateinit var btnVerCarrito: MaterialButton

    private lateinit var productosAdapter: ProductosMenuAdapter
    private lateinit var combosAdapter: CombosMenuAdapter

    private var mostrandoCombos: Boolean = false

    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "MX"))

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_nuevo_pedido_takeaway, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[PedidosClienteViewModel::class.java]

        // Verificar sucursal
        val reservasViewModel = ViewModelProvider(requireActivity())[ReservasClienteViewModel::class.java]
        val sucursal = reservasViewModel.getSucursalValue()

        if (sucursal == null) {
            Toast.makeText(requireContext(), "Selecciona una sucursal primero", Toast.LENGTH_LONG).show()
            return
        }

        viewModel.setSucursalId(sucursal.id_sucursal)

        setupViews(view)
        setupAdapters()
        setupListeners()
        observeViewModel()

        // Mostrar sucursal
        tvSucursal.text = sucursal.nombre

        // Cargar menú
        viewModel.loadCategorias()
        viewModel.loadProductos()
        viewModel.loadCombos()
    }

    private fun setupViews(view: View) {
        tvSucursal = view.findViewById(R.id.tvSucursal)
        chipGroupCategorias = view.findViewById(R.id.chipGroupCategorias)
        chipCombos = view.findViewById(R.id.chipCombos)
        recyclerProductos = view.findViewById(R.id.recyclerProductos)
        progressBar = view.findViewById(R.id.progressBar)
        layoutEmpty = view.findViewById(R.id.layoutEmpty)

        layoutCarrito = view.findViewById(R.id.layoutCarrito)
        tvCantidadCarrito = view.findViewById(R.id.tvCantidadCarrito)
        tvTotalCarrito = view.findViewById(R.id.tvTotalCarrito)
        btnVerCarrito = view.findViewById(R.id.btnVerCarrito)

        layoutCarrito.visibility = View.GONE
    }

    private fun setupAdapters() {
        productosAdapter = ProductosMenuAdapter { producto ->
            agregarProducto(producto)
        }

        combosAdapter = CombosMenuAdapter { combo ->
            agregarCombo(combo)
        }

        recyclerProductos.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerProductos.adapter = productosAdapter
    }

    private fun setupListeners() {
        chipCombos.setOnClickListener {
            mostrandoCombos = true
            chipCombos.isChecked = true
            limpiarSeleccionCategorias()
            mostrarCombos()
        }

        btnVerCarrito.setOnClickListener {
            mostrarCarritoDialog()
        }

        layoutCarrito.setOnClickListener {
            mostrarCarritoDialog()
        }
    }

    private fun observeViewModel() {
        // Categorías
        viewModel.categoriasState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is CategoriasState.Success -> {
                    crearChipsCategorias(state.categorias)
                }
                else -> {}
            }
        }

        // Productos
        viewModel.productosState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ProductosState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    recyclerProductos.visibility = View.GONE
                    layoutEmpty.visibility = View.GONE
                }
                is ProductosState.Success -> {
                    progressBar.visibility = View.GONE
                    if (!mostrandoCombos) {
                        mostrarProductos()
                    }
                }
                is ProductosState.Empty -> {
                    progressBar.visibility = View.GONE
                    recyclerProductos.visibility = View.GONE
                    layoutEmpty.visibility = View.VISIBLE
                }
                is ProductosState.Error -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        // Combos
        viewModel.combosState.observe(viewLifecycleOwner) { state ->
            if (mostrandoCombos && state is CombosState.Success) {
                mostrarCombos()
            }
        }

        // Carrito
        viewModel.carritoItems.observe(viewLifecycleOwner) { items ->
            actualizarBarraCarrito()
        }

        viewModel.totalCarrito.observe(viewLifecycleOwner) { total ->
            tvTotalCarrito.text = currencyFormat.format(total)
        }

        // Crear pedido
        viewModel.crearPedidoState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is CrearPedidoClienteState.Loading -> {
                    // Mostrar loading
                }
                is CrearPedidoClienteState.Success -> {
                    Toast.makeText(requireContext(), "✓ Pedido creado exitosamente", Toast.LENGTH_SHORT).show()
                    viewModel.resetCrearPedidoState()
                    (parentFragment as? PedidosClienteFragment)?.irAMisPedidos()
                }
                is CrearPedidoClienteState.Error -> {
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    viewModel.resetCrearPedidoState()
                }
                else -> {}
            }
        }
    }

    private fun crearChipsCategorias(categorias: List<com.intellisoft.androidcuisine.data.remote.dto.CategoriaDto>) {
        chipGroupCategorias.removeAllViews()

        // Chip "Todas"
        val chipTodas = Chip(requireContext()).apply {
            text = "Todas"
            isCheckable = true
            isChecked = true
            setOnClickListener {
                mostrandoCombos = false
                chipCombos.isChecked = false
                viewModel.seleccionarCategoria(null)
                mostrarProductos()
            }
        }
        chipGroupCategorias.addView(chipTodas)

        // Chips de categorías
        categorias.forEach { categoria ->
            val chip = Chip(requireContext()).apply {
                text = categoria.nombre
                isCheckable = true
                tag = categoria.id_categoria
                setOnClickListener {
                    mostrandoCombos = false
                    chipCombos.isChecked = false
                    limpiarSeleccionCategorias()
                    isChecked = true
                    viewModel.seleccionarCategoria(categoria.id_categoria)
                    mostrarProductos()
                }
            }
            chipGroupCategorias.addView(chip)
        }
    }

    private fun limpiarSeleccionCategorias() {
        for (i in 0 until chipGroupCategorias.childCount) {
            val chip = chipGroupCategorias.getChildAt(i) as? Chip
            chip?.isChecked = false
        }
    }

    private fun mostrarProductos() {
        recyclerProductos.adapter = productosAdapter
        val productos = viewModel.getProductosFiltrados()
        if (productos.isEmpty()) {
            recyclerProductos.visibility = View.GONE
            layoutEmpty.visibility = View.VISIBLE
        } else {
            recyclerProductos.visibility = View.VISIBLE
            layoutEmpty.visibility = View.GONE
            productosAdapter.submitList(productos)
        }
    }

    private fun mostrarCombos() {
        recyclerProductos.adapter = combosAdapter
        val state = viewModel.combosState.value
        if (state is CombosState.Success) {
            if (state.combos.isEmpty()) {
                recyclerProductos.visibility = View.GONE
                layoutEmpty.visibility = View.VISIBLE
            } else {
                recyclerProductos.visibility = View.VISIBLE
                layoutEmpty.visibility = View.GONE
                combosAdapter.submitList(state.combos)
            }
        }
    }

    private fun agregarProducto(producto: ProductoDto) {
        viewModel.agregarAlCarrito(producto)
        Toast.makeText(requireContext(), "✓ ${producto.nombre} agregado", Toast.LENGTH_SHORT).show()
    }

    private fun agregarCombo(combo: ComboDto) {
        viewModel.agregarComboAlCarrito(combo)
        Toast.makeText(requireContext(), "✓ ${combo.nombre} agregado", Toast.LENGTH_SHORT).show()
    }

    private fun actualizarBarraCarrito() {
        val cantidad = viewModel.getCantidadEnCarrito()
        if (cantidad > 0) {
            layoutCarrito.visibility = View.VISIBLE
            tvCantidadCarrito.text = "$cantidad items"
        } else {
            layoutCarrito.visibility = View.GONE
        }
    }

    private fun mostrarCarritoDialog() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_carrito, null)

        val recyclerCarrito = view.findViewById<RecyclerView>(R.id.recyclerCarrito)
        val tvTotal = view.findViewById<MaterialTextView>(R.id.tvTotalDialog)
        val etNotas = view.findViewById<TextInputEditText>(R.id.etNotas)
        val btnConfirmar = view.findViewById<MaterialButton>(R.id.btnConfirmarPedido)
        val btnLimpiar = view.findViewById<MaterialButton>(R.id.btnLimpiar)

        val carritoAdapter = CarritoAdapter(
            onActualizarCantidad = { item, cantidad -> viewModel.actualizarCantidad(item, cantidad) },
            onEliminar = { item -> viewModel.eliminarDelCarrito(item) }
        )

        recyclerCarrito.layoutManager = LinearLayoutManager(requireContext())
        recyclerCarrito.adapter = carritoAdapter

        // Observar cambios en el carrito
        viewModel.carritoItems.observe(viewLifecycleOwner) { items ->
            carritoAdapter.submitList(items.toList())
            if (items.isEmpty()) {
                dialog.dismiss()
            }
        }

        viewModel.totalCarrito.observe(viewLifecycleOwner) { total ->
            tvTotal.text = "Total: ${currencyFormat.format(total)}"
        }

        // Cargar datos iniciales
        carritoAdapter.submitList(viewModel.carritoItems.value?.toList() ?: emptyList())
        tvTotal.text = "Total: ${currencyFormat.format(viewModel.totalCarrito.value ?: 0.0)}"

        btnConfirmar.setOnClickListener {
            val notas = etNotas.text.toString().trim().ifEmpty { null }
            viewModel.crearPedidoTakeaway(notas)
            dialog.dismiss()
        }

        btnLimpiar.setOnClickListener {
            viewModel.limpiarCarrito()
            dialog.dismiss()
        }

        dialog.setContentView(view)
        dialog.show()
    }
}
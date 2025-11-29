package com.intellisoft.androidcuisine.views.compras

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.data.remote.dto.CompraDetalleRequest
import com.intellisoft.androidcuisine.data.remote.dto.CompraRequest
import com.intellisoft.androidcuisine.data.remote.dto.InsumoExistenciaDto
import com.intellisoft.androidcuisine.data.remote.dto.ProveedorDto
import com.intellisoft.androidcuisine.util.SessionManager
import com.intellisoft.androidcuisine.views.adapters.ArticulosCompraAdapter
import java.text.NumberFormat
import java.util.*

class CrearCompraFragment : Fragment() {

    private lateinit var viewModel: ComprasViewModel
    private lateinit var sessionManager: SessionManager

    private lateinit var actvProveedor: AutoCompleteTextView
    private lateinit var recyclerViewArticulos: RecyclerView
    private lateinit var tvNoArticulos: TextView
    private lateinit var tvTotal: TextView
    private lateinit var layoutTotal: View
    private lateinit var dividerTotal: View
    private lateinit var btnAgregarArticulo: MaterialButton
    private lateinit var btnGuardarCompra: MaterialButton
    private lateinit var btnCancelar: MaterialButton

    private lateinit var articulosAdapter: ArticulosCompraAdapter
    private val articulos = mutableListOf<ArticuloCompra>()

    private var proveedorSeleccionadoId: Int? = null

    private var proveedoresList: List<ProveedorDto> = emptyList()
    private var insumosList: List<InsumoExistenciaDto> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_crear_compra, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[ComprasViewModel::class.java]
        sessionManager = SessionManager.getInstance(requireContext())

        setupViews(view)
        setupRecyclerView()
        setupListeners()
        observeViewModel()
        setupProveedorDropdown()
        cargarProveedoresEInsumos()
    }

    private fun setupViews(view: View) {
        actvProveedor = view.findViewById(R.id.actvProveedor)
        recyclerViewArticulos = view.findViewById(R.id.recyclerViewArticulos)
        tvNoArticulos = view.findViewById(R.id.tvNoArticulos)
        tvTotal = view.findViewById(R.id.tvTotal)
        layoutTotal = view.findViewById(R.id.layoutTotal)
        dividerTotal = view.findViewById(R.id.dividerTotal)
        btnAgregarArticulo = view.findViewById(R.id.btnAgregarArticulo)
        btnGuardarCompra = view.findViewById(R.id.btnGuardarCompra)
        btnCancelar = view.findViewById(R.id.btnCancelar)
    }

    private fun setupRecyclerView() {
        articulosAdapter = ArticulosCompraAdapter(
            articulos = articulos,
            onDeleteClick = { articulo ->
                articulos.remove(articulo)
                articulosAdapter.notifyDataSetChanged()
                actualizarVista()
            }
        )
        recyclerViewArticulos.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewArticulos.adapter = articulosAdapter
    }

    private fun setupListeners() {
        btnAgregarArticulo.setOnClickListener {
            showAgregarArticuloDialog()
        }

        btnGuardarCompra.setOnClickListener {
            guardarCompra()
        }

        btnCancelar.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun observeViewModel() {
        viewModel.crearCompraState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is CrearCompraState.Loading -> {
                    btnGuardarCompra.isEnabled = false
                    btnGuardarCompra.text = "Guardando..."
                }
                is CrearCompraState.Success -> {
                    Toast.makeText(requireContext(), "Compra creada: ${state.folio}", Toast.LENGTH_LONG).show()
                    parentFragmentManager.popBackStack()
                }
                is CrearCompraState.Error -> {
                    btnGuardarCompra.isEnabled = true
                    btnGuardarCompra.text = "Guardar Compra"
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                }
                else -> {}
            }
        }

        viewModel.proveedoresState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ProveedoresState.Success -> {
                    proveedoresList = state.proveedores
                    setupProveedorDropdown()
                }
                is ProveedoresState.Error -> {
                    Toast.makeText(requireContext(), "Error al cargar proveedores: ${state.message}", Toast.LENGTH_LONG).show()
                }
                else -> {}
            }
        }

        viewModel.insumosState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is InsumosState.Success -> {
                    insumosList = state.insumos
                }
                is InsumosState.Error -> {
                    Toast.makeText(requireContext(), "Error al cargar insumos: ${state.message}", Toast.LENGTH_LONG).show()
                }
                else -> {}
            }
        }
    }

    private fun cargarProveedoresEInsumos() {
        viewModel.loadProveedores()

        val sucursales = sessionManager.getUserSucursales()
        if (sucursales.isNotEmpty()) {
            val sucursalId = sucursales[0].id_sucursal
            viewModel.loadInsumos(sucursalId)
        }
    }

    private fun setupProveedorDropdown() {
        if (proveedoresList.isEmpty()) return

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            proveedoresList.map { it.nombre }
        )

        actvProveedor.setAdapter(adapter)
        actvProveedor.setOnItemClickListener { _, _, position, _ ->
            proveedorSeleccionadoId = proveedoresList[position].id_proveedor
            validarFormulario()
        }
    }

    private fun showAgregarArticuloDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_agregar_articulo, null)

        val actvInsumo = dialogView.findViewById<AutoCompleteTextView>(R.id.actvInsumo)
        val etCantidad = dialogView.findViewById<TextInputEditText>(R.id.etCantidad)
        val etPresentacion = dialogView.findViewById<TextInputEditText>(R.id.etPresentacion)
        val etCostoUnitario = dialogView.findViewById<TextInputEditText>(R.id.etCostoUnitario)

        // Configurar dropdown de insumos
        if (insumosList.isEmpty()) {
            Toast.makeText(requireContext(), "No hay insumos disponibles", Toast.LENGTH_SHORT).show()
            return
        }

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            insumosList.map { "${it.nombre} (${it.unidad_clave})" }
        )

        actvInsumo.setAdapter(adapter)

        var insumoSeleccionado: InsumoExistenciaDto? = null

        actvInsumo.setOnItemClickListener { _, _, position, _ ->
            insumoSeleccionado = insumosList[position]
            // Pre-llenar con costo promedio si existe
            if (insumoSeleccionado!!.costo_promedio > 0) {
                etCostoUnitario.setText(insumoSeleccionado!!.costo_promedio.toString())
            }
        }

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .create()

        dialogView.findViewById<View>(R.id.btnCancelar).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<View>(R.id.btnAgregar).setOnClickListener {
            val cantidad = etCantidad.text.toString().toDoubleOrNull()
            val presentacion = etPresentacion.text.toString().trim()
            val costoUnitario = etCostoUnitario.text.toString().toDoubleOrNull()

            if (insumoSeleccionado == null) {
                Toast.makeText(requireContext(), "Selecciona un insumo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (cantidad == null || cantidad <= 0) {
                Toast.makeText(requireContext(), "Ingresa una cantidad válida", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (presentacion.isEmpty()) {
                Toast.makeText(requireContext(), "Ingresa la presentación", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (costoUnitario == null || costoUnitario <= 0) {
                Toast.makeText(requireContext(), "Ingresa un costo válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val articulo = ArticuloCompra(
                insumoId = insumoSeleccionado!!.id_insumo,
                nombreInsumo = insumoSeleccionado!!.nombre,
                cantidad = cantidad,
                presentacion = presentacion,
                costoUnitario = costoUnitario
            )

            articulos.add(articulo)
            articulosAdapter.notifyDataSetChanged()
            actualizarVista()

            dialog.dismiss()
        }

        dialog.show()
    }

    private fun actualizarVista() {
        if (articulos.isEmpty()) {
            tvNoArticulos.visibility = View.VISIBLE
            recyclerViewArticulos.visibility = View.GONE
            layoutTotal.visibility = View.GONE
            dividerTotal.visibility = View.GONE
        } else {
            tvNoArticulos.visibility = View.GONE
            recyclerViewArticulos.visibility = View.VISIBLE
            layoutTotal.visibility = View.VISIBLE
            dividerTotal.visibility = View.VISIBLE

            val total = articulos.sumOf { it.subtotal }
            tvTotal.text = formatCurrency(total)
        }

        validarFormulario()
    }

    private fun validarFormulario() {
        btnGuardarCompra.isEnabled = proveedorSeleccionadoId != null && articulos.isNotEmpty()
    }

    private fun guardarCompra() {
        val sucursales = sessionManager.getUserSucursales()
        if (sucursales.isEmpty()) {
            Toast.makeText(requireContext(), "No tienes sucursales asignadas", Toast.LENGTH_LONG).show()
            return
        }

        val sucursalId = sucursales[0].id_sucursal

        val detalles = articulos.map { articulo ->
            CompraDetalleRequest(
                insumo_id = articulo.insumoId,
                cant_presentacion = articulo.cantidad,
                costo_unit_present = articulo.costoUnitario,
                presentacion = articulo.presentacion
            )
        }

        val compraRequest = CompraRequest(
            sucursal_id = sucursalId,
            proveedor_id = proveedorSeleccionadoId!!,
            detalles = detalles
        )

        viewModel.crearCompra(compraRequest)
    }

    private fun formatCurrency(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
        return format.format(amount)
    }
}
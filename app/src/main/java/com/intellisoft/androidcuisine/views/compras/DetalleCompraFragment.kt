package com.intellisoft.androidcuisine.views.compras

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.data.remote.dto.CompraDetalleCompleta
import com.intellisoft.androidcuisine.views.adapters.CompraDetallesAdapter
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class DetalleCompraFragment : Fragment() {

    private lateinit var viewModel: ComprasViewModel

    private lateinit var progressBar: ProgressBar
    private lateinit var contentLayout: LinearLayout
    private lateinit var tvFolio: TextView
    private lateinit var tvFecha: TextView
    private lateinit var chipEstado: Chip
    private lateinit var tvProveedor: TextView
    private lateinit var tvSucursal: TextView
    private lateinit var tvUsuario: TextView
    private lateinit var recyclerViewDetalles: RecyclerView
    private lateinit var tvTotal: TextView
    private lateinit var btnCancelar: MaterialButton

    private lateinit var detallesAdapter: CompraDetallesAdapter

    private var compraId: Int = -1

    companion object {
        private const val ARG_COMPRA_ID = "compra_id"

        fun newInstance(compraId: Int): DetalleCompraFragment {
            val fragment = DetalleCompraFragment()
            val args = Bundle()
            args.putInt(ARG_COMPRA_ID, compraId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        compraId = arguments?.getInt(ARG_COMPRA_ID) ?: -1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detalle_compra, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[ComprasViewModel::class.java]

        setupViews(view)
        setupRecyclerView()
        setupListeners()
        observeViewModel()

        if (compraId != -1) {
            viewModel.loadDetalleCompra(compraId)
        }
    }

    private fun setupViews(view: View) {
        progressBar = view.findViewById(R.id.progressBar)
        contentLayout = view.findViewById(R.id.contentLayout)
        tvFolio = view.findViewById(R.id.tvFolio)
        tvFecha = view.findViewById(R.id.tvFecha)
        chipEstado = view.findViewById(R.id.chipEstado)
        tvProveedor = view.findViewById(R.id.tvProveedor)
        tvSucursal = view.findViewById(R.id.tvSucursal)
        tvUsuario = view.findViewById(R.id.tvUsuario)
        recyclerViewDetalles = view.findViewById(R.id.recyclerViewDetalles)
        tvTotal = view.findViewById(R.id.tvTotal)
        btnCancelar = view.findViewById(R.id.btnCancelar)
    }

    private fun setupRecyclerView() {
        detallesAdapter = CompraDetallesAdapter()
        recyclerViewDetalles.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewDetalles.adapter = detallesAdapter
    }

    private fun setupListeners() {
        btnCancelar.setOnClickListener {
            showCancelarDialog()
        }
    }

    private fun observeViewModel() {
        viewModel.detalleCompraState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DetalleCompraState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    contentLayout.visibility = View.GONE
                }
                is DetalleCompraState.Success -> {
                    progressBar.visibility = View.GONE
                    contentLayout.visibility = View.VISIBLE
                    mostrarDetalle(state.detalle)
                }
                is DetalleCompraState.Error -> {
                    progressBar.visibility = View.GONE
                    contentLayout.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun mostrarDetalle(detalle: CompraDetalleCompleta) {
        tvFolio.text = detalle.folio
        tvFecha.text = "Fecha: ${formatFecha(detalle.fecha_compra)}"
        tvProveedor.text = "Proveedor: ${detalle.proveedor.nombre}"
        tvSucursal.text = "Sucursal: ${detalle.sucursal.nombre}"
        tvUsuario.text = "Registrado por: ${detalle.usuario.nombre} ${detalle.usuario.apellido}"
        tvTotal.text = formatCurrency(detalle.total_compra)

        configurarEstado(detalle.estatus)
        detallesAdapter.submitList(detalle.detalles)

        // Mostrar botón cancelar solo si está en estatus pendiente
        btnCancelar.visibility = if (detalle.estatus == 1) View.VISIBLE else View.GONE
    }

    private fun configurarEstado(estatus: Int) {
        when (estatus) {
            1 -> {
                chipEstado.text = "Pendiente"
                chipEstado.setChipBackgroundColorResource(R.color.cuisine_green_light)
                chipEstado.setTextColor(requireContext().getColor(R.color.cuisine_dark_text))
            }
            2 -> {
                chipEstado.text = "Recibida"
                chipEstado.setChipBackgroundColorResource(R.color.cuisine_green_dark)
                chipEstado.setTextColor(requireContext().getColor(R.color.white))
            }
            3 -> {
                chipEstado.text = "Cancelada"
                chipEstado.setChipBackgroundColorResource(R.color.cuisine_beige_background)
                chipEstado.setTextColor(requireContext().getColor(R.color.cuisine_dark_text))
            }
        }
    }

    private fun showCancelarDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Cancelar Compra")
            .setMessage("¿Estás seguro que deseas cancelar esta compra?")
            .setPositiveButton("Cancelar Compra") { _, _ ->
                viewModel.cancelarCompra(compraId)
                Toast.makeText(requireContext(), "Compra cancelada", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun formatFecha(fecha: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val date = inputFormat.parse(fecha)
            date?.let { outputFormat.format(it) } ?: fecha
        } catch (e: Exception) {
            fecha
        }
    }

    private fun formatCurrency(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
        return format.format(amount)
    }
}
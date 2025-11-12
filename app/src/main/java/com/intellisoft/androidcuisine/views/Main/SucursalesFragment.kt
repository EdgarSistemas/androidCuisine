package com.intellisoft.androidcuisine.views.Main

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.data.remote.api.ApiClient
import com.intellisoft.androidcuisine.data.remote.dto.Sucursal
import com.intellisoft.androidcuisine.data.remote.dto.SucursalRequest
import com.intellisoft.androidcuisine.views.adapters.SucursalesAdapter
import kotlinx.coroutines.launch

class SucursalesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView
    private lateinit var fabAdd: FloatingActionButton

    private lateinit var adapter: SucursalesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sucursales, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupRecyclerView()
        setupClickListeners()
        loadSucursales()
    }

    private fun initViews(view: View) {
        recyclerView = view.findViewById(R.id.recyclerViewSucursales)
        swipeRefresh = view.findViewById(R.id.swipeRefresh)
        progressBar = view.findViewById(R.id.progressBar)
        tvEmpty = view.findViewById(R.id.tvEmpty)
        fabAdd = view.findViewById(R.id.fabAddSucursal)
    }

    private fun setupRecyclerView() {
        adapter = SucursalesAdapter(
            onEditClick = { sucursal -> showEditDialog(sucursal) },
            onDeleteClick = { sucursal -> showDeleteConfirmation(sucursal) }
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun setupClickListeners() {
        fabAdd.setOnClickListener {
            showAddDialog()
        }

        swipeRefresh.setOnRefreshListener {
            loadSucursales()
        }
    }

    private fun loadSucursales() {
        showLoading(true)

        lifecycleScope.launch {
            try {
                Log.d("SucursalesFragment", "🔄 Cargando sucursales...")

                val response = ApiClient.sucursalService.getSucursalesActivas()

                if (response.isSuccessful) {
                    val sucursalesResponse = response.body()
                    if (sucursalesResponse?.success == true) {
                        val sucursales = sucursalesResponse.data ?: emptyList()

                        Log.d("SucursalesFragment", "✅ Sucursales cargadas: ${sucursales.size}")

                        adapter.submitList(sucursales)
                        showEmpty(sucursales.isEmpty())
                    } else {
                        Log.e("SucursalesFragment", "❌ Error en respuesta: ${sucursalesResponse?.message}")
                        showError("Error en la respuesta del servidor")
                    }
                } else {
                    Log.e("SucursalesFragment", "❌ Error HTTP: ${response.code()}")
                    showError("Error de conexión: ${response.code()}")
                }

            } catch (e: Exception) {
                Log.e("SucursalesFragment", "❌ Error: ${e.message}")
                e.printStackTrace()
                showError("Error de conexión: ${e.message}")
            } finally {
                showLoading(false)
                swipeRefresh.isRefreshing = false
            }
        }
    }

    private fun showAddDialog() {
        showSucursalDialog(null)
    }

    private fun showEditDialog(sucursal: Sucursal) {
        showSucursalDialog(sucursal)
    }

    private fun showSucursalDialog(sucursal: Sucursal?) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_sucursal, null)

        val tvTitle = dialogView.findViewById<TextView>(R.id.tvDialogTitle)
        val etNombre = dialogView.findViewById<TextInputEditText>(R.id.etNombre)
        val etDireccion = dialogView.findViewById<TextInputEditText>(R.id.etDireccion)
        val etTelefono = dialogView.findViewById<TextInputEditText>(R.id.etTelefono)
        val btnCancelar = dialogView.findViewById<Button>(R.id.btnCancelar)
        val btnGuardar = dialogView.findViewById<Button>(R.id.btnGuardar)

        // Configurar título y datos
        if (sucursal != null) {
            tvTitle.text = "Editar Sucursal"
            etNombre.setText(sucursal.nombre)
            etDireccion.setText(sucursal.direccion)
            etTelefono.setText(sucursal.telefono)
            btnGuardar.text = "Actualizar"
        } else {
            tvTitle.text = "Agregar Sucursal"
            btnGuardar.text = "Crear"
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        btnCancelar.setOnClickListener {
            dialog.dismiss()
        }

        btnGuardar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val direccion = etDireccion.text.toString().trim()
            val telefono = etTelefono.text.toString().trim()

            if (validateInput(nombre, direccion, telefono)) {
                val sucursalRequest = SucursalRequest(
                    nombre = nombre,
                    direccion = direccion,
                    telefono = telefono
                )

                if (sucursal != null) {
                    updateSucursal(sucursal.id_sucursal, sucursalRequest)
                } else {
                    createSucursal(sucursalRequest)
                }

                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun validateInput(nombre: String, direccion: String, telefono: String): Boolean {
        if (nombre.isEmpty()) {
            showError("El nombre es obligatorio")
            return false
        }
        if (direccion.isEmpty()) {
            showError("La dirección es obligatoria")
            return false
        }
        if (telefono.isEmpty()) {
            showError("El teléfono es obligatorio")
            return false
        }
        return true
    }

    private fun createSucursal(sucursalRequest: SucursalRequest) {
        lifecycleScope.launch {
            try {
                Log.d("SucursalesFragment", "➕ Creando sucursal: ${sucursalRequest.nombre}")

                val response = ApiClient.sucursalService.createSucursal(sucursalRequest)

                if (response.isSuccessful) {
                    val result = response.body()
                    if (result?.success == true) {
                        Log.d("SucursalesFragment", "✅ Sucursal creada: ${result.message}")
                        showSuccess("Sucursal creada exitosamente")
                        loadSucursales() // Recargar lista
                    } else {
                        showError("Error: ${result?.message}")
                    }
                } else {
                    showError("Error HTTP: ${response.code()}")
                }

            } catch (e: Exception) {
                Log.e("SucursalesFragment", "❌ Error creando sucursal: ${e.message}")
                showError("Error de conexión: ${e.message}")
            }
        }
    }

    private fun updateSucursal(id: Int, sucursalRequest: SucursalRequest) {
        lifecycleScope.launch {
            try {
                Log.d("SucursalesFragment", "✏️ Actualizando sucursal $id: ${sucursalRequest.nombre}")

                val response = ApiClient.sucursalService.updateSucursal(id, sucursalRequest)

                if (response.isSuccessful) {
                    val result = response.body()
                    if (result?.success == true) {
                        Log.d("SucursalesFragment", "✅ Sucursal actualizada: ${result.message}")
                        showSuccess("Sucursal actualizada exitosamente")
                        loadSucursales() // Recargar lista
                    } else {
                        showError("Error: ${result?.message}")
                    }
                } else {
                    showError("Error HTTP: ${response.code()}")
                }

            } catch (e: Exception) {
                Log.e("SucursalesFragment", "❌ Error actualizando sucursal: ${e.message}")
                showError("Error de conexión: ${e.message}")
            }
        }
    }

    private fun showDeleteConfirmation(sucursal: Sucursal) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar Sucursal")
            .setMessage("¿Estás seguro que deseas eliminar la sucursal '${sucursal.nombre}'?")
            .setPositiveButton("Eliminar") { _, _ ->
                deleteSucursal(sucursal)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun deleteSucursal(sucursal: Sucursal) {
        lifecycleScope.launch {
            try {
                Log.d("SucursalesFragment", "🗑️ Eliminando sucursal: ${sucursal.nombre}")

                val response = ApiClient.sucursalService.deleteSucursal(sucursal.id_sucursal)

                if (response.isSuccessful) {
                    val result = response.body()
                    if (result?.success == true) {
                        Log.d("SucursalesFragment", "✅ Sucursal eliminada: ${result.message}")
                        showSuccess("Sucursal eliminada exitosamente")
                        loadSucursales() // Recargar lista
                    } else {
                        showError("Error: ${result?.message}")
                    }
                } else {
                    showError("Error HTTP: ${response.code()}")
                }

            } catch (e: Exception) {
                Log.e("SucursalesFragment", "❌ Error eliminando sucursal: ${e.message}")
                showError("Error de conexión: ${e.message}")
            }
        }
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun showEmpty(show: Boolean) {
        tvEmpty.visibility = if (show) View.VISIBLE else View.GONE
        recyclerView.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), "❌ $message", Toast.LENGTH_LONG).show()
    }

    private fun showSuccess(message: String) {
        Toast.makeText(requireContext(), "✅ $message", Toast.LENGTH_SHORT).show()
    }
}

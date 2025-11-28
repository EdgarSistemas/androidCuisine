package com.intellisoft.androidcuisine.views.usuarios

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.intellisoft.androidcuisine.data.remote.dto.UsuarioCreateRequest
import com.intellisoft.androidcuisine.data.remote.dto.UsuarioDto
import com.intellisoft.androidcuisine.data.remote.dto.UsuarioUpdateRequest
import com.intellisoft.androidcuisine.databinding.DialogAgregarEditarUsuarioBinding
import com.intellisoft.androidcuisine.databinding.FragmentUsuariosBinding
import com.intellisoft.androidcuisine.util.SessionManager
import com.intellisoft.androidcuisine.views.adapters.UsuariosAdapter

class UsuariosFragment : Fragment() {

    private var _binding: FragmentUsuariosBinding? = null
    private val binding get() = _binding!!

    // Instancia del ViewModel
    private val viewModel: UsuariosViewModel by viewModels()

    private lateinit var adapter: UsuariosAdapter
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUsuariosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar SessionManager
        sessionManager = SessionManager(requireContext())

        // Obtener sucursal actual (usando la función que agregamos a SessionManager)
        val sucursalId = sessionManager.getSucursalId()

        setupRecyclerView()
        setupListeners(sucursalId)
        setupObservers(sucursalId)

        // Cargar datos iniciales
        viewModel.cargarDatosIniciales(sucursalId)
    }

    private fun setupRecyclerView() {
        adapter = UsuariosAdapter(
            emptyList(),
            onEditClick = { usuario -> mostrarDialogoUsuario(usuario) },
            onDeleteClick = { usuario -> confirmarEliminacion(usuario) }
        )
        binding.rvUsuarios.layoutManager = LinearLayoutManager(context)
        binding.rvUsuarios.adapter = adapter
    }

    private fun setupListeners(sucursalId: Int) {
        binding.fabAgregarUsuario.setOnClickListener {
            mostrarDialogoUsuario(null) // null indica modo "Crear"
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.listarUsuarios(sucursalId)
            binding.swipeRefresh.isRefreshing = false // Ocultar spinner de refresh inmediatamente
        }
    }

    private fun setupObservers(sucursalId: Int) {
        // Observar lista de usuarios
        viewModel.usuarios.observe(viewLifecycleOwner) { lista ->
            adapter.updateList(lista)
            // Manejar estado de lista vacía visualmente si quisieras
        }

        // Observar estado de carga (ProgressBar)
        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        // Observar mensajes (Toasts)
        viewModel.message.observe(viewLifecycleOwner) { msg ->
            msg?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                viewModel.clearMessage()
            }
        }

        // Observar éxito de operaciones para recargar la lista
        viewModel.operationSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                viewModel.listarUsuarios(sucursalId)
                viewModel.resetOperation()
            }
        }
    }

    private fun mostrarDialogoUsuario(usuario: UsuarioDto?) {
        val dialogBinding = DialogAgregarEditarUsuarioBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 1. Configurar Spinner
        val rolesDisponibles = viewModel.roles.value ?: emptyList()
        val adapterRoles = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, rolesDisponibles.map { it.nombre })
        adapterRoles.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dialogBinding.spinnerRoles.adapter = adapterRoles

        if (usuario != null) {
            // === MODO EDITAR ===
            dialogBinding.tvTituloDialog.text = "Editar Usuario"
            dialogBinding.etNombre.setText(usuario.nombre)
            dialogBinding.etApellido.setText(usuario.apellido)
            dialogBinding.etEmail.setText(usuario.email)
            dialogBinding.etTelefono.setText(usuario.telefono ?: "")

            dialogBinding.tilPassword.visibility = View.GONE
            dialogBinding.spinnerRoles.isEnabled = false

            // CORRECCIÓN 1: Usamos 'usuario.rolId' (según tu UsuarioDto) vs 'it.idRol' (según tu RolDto)
            val rolIndex = rolesDisponibles.indexOfFirst { it.id_rol == usuario.rolId }
            if (rolIndex >= 0) {
                dialogBinding.spinnerRoles.setSelection(rolIndex)
            }

            dialogBinding.btnGuardarUsuario.setOnClickListener {
                val req = UsuarioUpdateRequest(
                    nombre = dialogBinding.etNombre.text.toString(),
                    apellido = dialogBinding.etApellido.text.toString(),
                    email = dialogBinding.etEmail.text.toString()
                )
                // CORRECCIÓN 2: 'usuario.idUsuario' según tu UsuarioDto
                viewModel.actualizarUsuario(usuario.idUsuario, req)
                dialog.dismiss()
            }

        } else {
            // === MODO CREAR ===
            dialogBinding.tvTituloDialog.text = "Nuevo Usuario"
            dialogBinding.tilPassword.visibility = View.VISIBLE
            dialogBinding.spinnerRoles.isEnabled = true

            dialogBinding.btnGuardarUsuario.setOnClickListener {
                if (rolesDisponibles.isEmpty()) {
                    Toast.makeText(context, "No hay roles cargados", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val selectedPosition = dialogBinding.spinnerRoles.selectedItemPosition
                val rolSeleccionado = rolesDisponibles.getOrNull(selectedPosition)

                if (rolSeleccionado == null) {
                    Toast.makeText(context, "Seleccione un rol", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val req = UsuarioCreateRequest(
                    nombre = dialogBinding.etNombre.text.toString(),
                    apellido = dialogBinding.etApellido.text.toString(),
                    email = dialogBinding.etEmail.text.toString(),
                    password = dialogBinding.etPassword.text.toString(),
                    telefono = dialogBinding.etTelefono.text.toString(),

                    // CORRECCIÓN 3: Aquí tomamos el ID del objeto ROL seleccionado.
                    // Según tu RolDto, la propiedad es 'idRol'.
                    rolId = rolSeleccionado.id_rol,

                    sucursalId = sessionManager.getSucursalId()
                )

                viewModel.crearUsuario(req)
                dialog.dismiss()
            }
        }

        dialogBinding.btnCancelar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun confirmarEliminacion(usuario: UsuarioDto) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar Usuario")
            .setMessage("¿Estás seguro de que deseas eliminar a ${usuario.nombre} ${usuario.apellido}?")
            .setPositiveButton("Eliminar") { _, _ ->
                viewModel.eliminarUsuario(usuario.idUsuario)
            }
            .setNegativeButton("Cancelar", null)
            .create()
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
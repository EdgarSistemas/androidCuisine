package com.intellisoft.androidcuisine.views.Usuarios

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.data.remote.dto.RolItemDto
import com.intellisoft.androidcuisine.data.remote.dto.UsuarioDto
import com.intellisoft.androidcuisine.databinding.DialogAgregarEditarUsuarioBinding
import com.intellisoft.androidcuisine.databinding.FragmentUsuariosBinding
import com.intellisoft.androidcuisine.domain.repository.usuario.UsuarioRepositoryImpl
import com.intellisoft.androidcuisine.util.SessionManager
import com.intellisoft.androidcuisine.views.adapters.UsuariosAdapter

// SIN @AndroidEntryPoint
class UsuariosFragment : Fragment() {

    private var _binding: FragmentUsuariosBinding? = null
    private val binding get() = _binding!!

    // Declaramos el ViewModel pero NO lo inicializamos con 'by viewModels()'
    private lateinit var viewModel: UsuariosViewModel
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

        // 1. INICIALIZACIÓN MANUAL (Igual que antes)
        sessionManager = SessionManager(requireContext())
        val repository = UsuarioRepositoryImpl()

        // 2. FACTORY (Igual que antes)
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(UsuariosViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return UsuariosViewModel(repository, sessionManager) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }

        // 3. OBTENER VIEWMODEL (Igual que antes)
        viewModel = ViewModelProvider(this, factory)[UsuariosViewModel::class.java]

        setupRecyclerView()
        setupListeners()
        setupObservers()

        // === CORRECCIÓN AQUÍ ===
        // Antes tenías:
        // viewModel.cargarRoles()
        // viewModel.listarUsuarios(primerRol.id) <--- Error aquí

        // Ahora solo llamas a la función maestra:
        viewModel.cargarDatosIniciales()
        // Esta función del ViewModel ya se encarga internamente de buscar los roles,
        // encontrar el primero y cargar sus usuarios automáticamente.
    }

    private fun setupRecyclerView() {
        adapter = UsuariosAdapter(
            onEditClick = { usuario -> mostrarDialogoUsuario(usuario) },
            onDeleteClick = { usuario -> confirmarEliminacion(usuario) }
        )
        binding.rvUsuarios.layoutManager = LinearLayoutManager(context)
        binding.rvUsuarios.adapter = adapter
    }

    private fun setupListeners() {
        binding.fabAddUsuario.setOnClickListener {
            mostrarDialogoUsuario(null)
        }

        binding.swipeRefresh.setOnRefreshListener {
            // CORRECCIÓN: Usamos la variable guardada en el ViewModel
            viewModel.listarUsuarios(viewModel.rolSeleccionadoId)
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun setupObservers() {
        // 1. Observar lista de usuarios
        viewModel.usuarios.observe(viewLifecycleOwner) { lista ->
            adapter.submitList(lista)
            binding.tvEmptyView.isVisible = lista.isEmpty()
        }

        // 2. Observar carga
        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.isVisible = loading
        }

        // 3. Observar Mensajes
        viewModel.mensaje.observe(viewLifecycleOwner) { msg ->
            if (!msg.isNullOrEmpty()) {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                viewModel.limpiarMensaje()
            }
        }

        // 4. Observar Operación Exitosa
        viewModel.operacionExitosa.observe(viewLifecycleOwner) { success ->
            if (success) {
                // CORRECCIÓN: Usamos la variable guardada en el ViewModel
                viewModel.listarUsuarios(viewModel.rolSeleccionadoId)
                viewModel.resetOperacion()
            }
        }
        viewModel.roles.observe(viewLifecycleOwner) { roles ->
            if (roles.isNotEmpty()) {
                setupFiltroSpinner(roles)
            }
        }
    }
    private fun setupFiltroSpinner(roles: List<RolItemDto>) { // Asegúrate de usar tu clase Rol (RolDto o RolItemDto)
        // Creamos una lista solo con los nombres para mostrar
        val nombresRoles = roles.map { it.nombre }
        val adapterSpinner = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, nombresRoles)

        // Buscamos el spinner (si binding falla usa findViewById)
        val spinner = binding.root.findViewById<Spinner>(R.id.spFiltroRol)
        spinner.adapter = adapterSpinner

        // Listener: Cuando el usuario elige un rol, recargamos la lista
        spinner.onItemSelectedListener = object :AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val rolSeleccionado = roles[position]

                // Le avisamos al ViewModel el nuevo rol y pedimos recargar
                viewModel.rolSeleccionadoId = rolSeleccionado.id
                viewModel.listarUsuarios(rolSeleccionado.id)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
    private fun mostrarDialogoUsuario(usuario: UsuarioDto?) {
        val dialogBinding = DialogAgregarEditarUsuarioBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Configurar Spinner
        val rolesDisponibles = viewModel.roles.value ?: emptyList()
        val adapterRoles = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, rolesDisponibles.map { it.nombre })
        adapterRoles.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dialogBinding.spRoles.adapter = adapterRoles

        if (usuario != null) {
            // MODO EDITAR
            dialogBinding.tvTitulo.text = "Editar Usuario"
            dialogBinding.etNombreUsuario.setText(usuario.nombre)
            dialogBinding.etApellidoUsuario.setText(usuario.apellido)
            dialogBinding.etEmailUsuario.setText(usuario.email)
            dialogBinding.etTelefonoUsuario.setText(usuario.telefono ?: "")

            dialogBinding.tilPasswordUsuario.visibility = View.GONE
            dialogBinding.spRoles.isEnabled = true // Permitimos editar rol si quieres

            // Preseleccionar rol
            val rolActualId = usuario.roles?.firstOrNull()?.id
            val rolIndex = rolesDisponibles.indexOfFirst { it.id == rolActualId }
            if (rolIndex >= 0) dialogBinding.spRoles.setSelection(rolIndex)

            dialogBinding.btnGuardar.setOnClickListener {
                // Validación básica para editar
                if (dialogBinding.etNombreUsuario.text.toString().length < 2) {
                    dialogBinding.tilNombre.error = "Mínimo 2 letras"
                    return@setOnClickListener
                }

                viewModel.actualizarUsuario(
                    usuario.id,
                    dialogBinding.etNombreUsuario.text.toString(),
                    dialogBinding.etApellidoUsuario.text.toString(),
                    dialogBinding.etEmailUsuario.text.toString()
                )
                dialog.dismiss()
            }

        } else {
            // MODO CREAR
            dialogBinding.tvTitulo.text = "Nuevo Empleado"
            dialogBinding.tilPasswordUsuario.visibility = View.VISIBLE
            dialogBinding.spRoles.isEnabled = true

            dialogBinding.btnGuardar.setOnClickListener {
                // === VALIDACIONES DEL BACKEND ===
                val nombre = dialogBinding.etNombreUsuario.text.toString().trim()
                val apellido = dialogBinding.etApellidoUsuario.text.toString().trim()
                val email = dialogBinding.etEmailUsuario.text.toString().trim()
                val telefono = dialogBinding.etTelefonoUsuario.text.toString().trim()
                val password = dialogBinding.etPasswordUsuario.text.toString().trim()

                var isValid = true

                // 1. Nombre y Apellido
                if (nombre.length < 2) {
                    dialogBinding.tilNombre.error = "Mínimo 2 letras"
                    isValid = false
                } else dialogBinding.tilNombre.error = null

                if (apellido.length < 2) {
                    dialogBinding.tilApellido.error = "Mínimo 2 letras"
                    isValid = false
                } else dialogBinding.tilApellido.error = null

                // 2. Email
                if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    dialogBinding.tilEmail.error = "Correo inválido"
                    isValid = false
                } else dialogBinding.tilEmail.error = null

                // 3. Teléfono (Opcional pero validado si se escribe)
                if (telefono.isNotEmpty() && (telefono.length < 10 || telefono.length > 15)) {
                    dialogBinding.tilTelefono.error = "Entre 10 y 15 dígitos"
                    isValid = false
                } else dialogBinding.tilTelefono.error = null

                // 4. Password (CRÍTICO: Letra + Número)
                val tieneLetra = password.any { it.isLetter() }
                val tieneNumero = password.any { it.isDigit() }

                if (password.length < 6 || !tieneLetra || !tieneNumero) {
                    dialogBinding.tilPasswordUsuario.error = "Mín. 6 caracteres, 1 letra y 1 número"
                    isValid = false
                } else dialogBinding.tilPasswordUsuario.error = null

                if (!isValid) return@setOnClickListener

                // Obtener Rol Seleccionado (CORRECCIÓN FINAL DE LÓGICA)
                val pos = dialogBinding.spRoles.selectedItemPosition
                val rolDto = rolesDisponibles.getOrNull(pos)

                if (rolDto != null) {
                    viewModel.crearUsuario(
                        nombre, apellido, email, password, telefono, rolDto.id
                    )
                    dialog.dismiss()
                } else {
                    Toast.makeText(context, "Selecciona un rol", Toast.LENGTH_SHORT).show()
                }
            }
        }

        dialogBinding.btnCancelar.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun confirmarEliminacion(usuario: UsuarioDto) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar")
            .setMessage("¿Eliminar a ${usuario.nombre}?")
            .setPositiveButton("Sí") { _, _ -> viewModel.eliminarUsuario(usuario.id) }
            .setNegativeButton("No", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
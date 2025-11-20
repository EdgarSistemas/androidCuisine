package com.intellisoft.androidcuisine.views.Main.cuenta

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.textfield.TextInputEditText
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.util.SessionManager
import com.intellisoft.androidcuisine.views.Bienvenida.BienvenidaActivity // Asegúrate de importar tu Activity de inicio/login

class CuentaFragment : Fragment() {

    private val viewModel: CuentaViewModel by viewModels()
    private lateinit var sessionManager: SessionManager

    // Vistas de Información Personal
    private lateinit var etName: TextInputEditText
    private lateinit var etApellido: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPhone: TextInputEditText
    private lateinit var btnUpdate: Button

    // Vistas de Contraseña
    private lateinit var etCurrentPassword: TextInputEditText
    private lateinit var etNewPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var btnChangePassword: Button

    // Botón Logout
    private lateinit var btnLogout: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cuenta, container, false)
        sessionManager = SessionManager(requireContext())

        initViews(view)
        setupListeners()
        observeViewModel()

        return view
    }

    private fun initViews(view: View) {
        // 1. Información Personal (IDs del XML nuevo)
        etName = view.findViewById(R.id.etName)
        etApellido = view.findViewById(R.id.etApellido)
        etEmail = view.findViewById(R.id.etEmail)
       // etPhone = view.findViewById(R.id.etPhone)
        btnUpdate = view.findViewById(R.id.btnUpdate)

        // 2. Cambio de Contraseña
        etCurrentPassword = view.findViewById(R.id.etCurrentPassword)
        etNewPassword = view.findViewById(R.id.etNewPassword)
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword)
        btnChangePassword = view.findViewById(R.id.btnChangePassword)

        // 3. Logout
        btnLogout = view.findViewById(R.id.btnLogout)
    }

    private fun setupListeners() {
        // --- Listener Actualizar Datos ---
        btnUpdate.setOnClickListener {
            val nombre = etName.text.toString().trim()
            val apellido = etApellido.text.toString().trim()
            val email = etEmail.text.toString().trim()
            // El teléfono no se envía en updateUsuario por ahora, pero se podría agregar después

            viewModel.updateUsuario(nombre, apellido, email)
        }

        // --- Listener Cambiar Contraseña (Validación UI) ---
        btnChangePassword.setOnClickListener {
            val currentPass = etCurrentPassword.text.toString().trim()
            val newPass = etNewPassword.text.toString().trim()
            val confirmPass = etConfirmPassword.text.toString().trim()

            if (currentPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(context, "Por favor llena todos los campos de contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPass != confirmPass) {
                Toast.makeText(context, "Las nuevas contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // AQUÍ LLAMARÍAS AL VIEWMODEL PARA CAMBIAR LA CONTRASEÑA
            // viewModel.cambiarPassword(currentPass, newPass)
            Toast.makeText(context, "Funcionalidad de cambiar contraseña pendiente de Backend", Toast.LENGTH_SHORT).show()
        }

        // --- Listener Cerrar Sesión ---
        btnLogout.setOnClickListener {
            logout()
        }
    }

    private fun observeViewModel() {
        // Observar datos del usuario actual para llenar los campos
        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            user?.let {
                etName.setText(it.nombre)
                etApellido.setText(it.apellido)
                etEmail.setText(it.email)
                etPhone.setText(it.telefono ?: "") // Llenamos teléfono si existe en el objeto User
            }
        }

        // Observar estado de la actualización
        viewModel.updateState.observe(viewLifecycleOwner) { state ->
            when (state) {
                UpdateState.Loading -> {
                    btnUpdate.isEnabled = false
                    btnUpdate.text = "Actualizando..."
                }
                is UpdateState.Success -> {
                    btnUpdate.isEnabled = true
                    btnUpdate.text = "Actualizar Datos"
                    Toast.makeText(requireContext(), "✅ ${state.message}", Toast.LENGTH_SHORT).show()
                    viewModel.resetState()
                }
                is UpdateState.Error -> {
                    btnUpdate.isEnabled = true
                    btnUpdate.text = "Actualizar Datos"
                    Toast.makeText(requireContext(), "❌ ${state.message}", Toast.LENGTH_LONG).show()
                    viewModel.resetState()
                }
                UpdateState.Idle -> {
                    btnUpdate.isEnabled = true
                    btnUpdate.text = "Actualizar Datos"
                }
            }
        }
    }

    private fun logout() {
        sessionManager.clearSession()
        // Navegar al Login y limpiar el stack para que no pueda volver atrás
        val intent = Intent(requireContext(), BienvenidaActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }
}
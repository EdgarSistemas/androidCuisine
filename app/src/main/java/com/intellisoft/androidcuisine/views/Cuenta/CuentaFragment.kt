package com.intellisoft.androidcuisine.views.cuenta

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
import com.intellisoft.androidcuisine.views.Bienvenida.BienvenidaActivity
import com.intellisoft.androidcuisine.views.Main.cuenta.CuentaViewModel
import com.intellisoft.androidcuisine.views.Main.cuenta.UpdateState

class CuentaFragment : Fragment() {

    private val viewModel: CuentaViewModel by viewModels()
    private lateinit var sessionManager: SessionManager

    // Vistas de Información Personal
    private lateinit var etName: TextInputEditText
    private lateinit var etApellido: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    // private lateinit var etPhone: TextInputEditText
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
        // 1. Información Personal
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

            viewModel.updateUsuario(nombre, apellido, email)
        }

        // --- Listener Cambiar Contraseña ---
        // CORREGIDO: Solo una definición limpia aquí
        btnChangePassword.setOnClickListener {
            val currentPass = etCurrentPassword.text.toString().trim()
            val newPass = etNewPassword.text.toString().trim()
            val confirmPass = etConfirmPassword.text.toString().trim()

            // Validaciones de UI rápidas
            if (currentPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(context, "Por favor llena todos los campos de contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPass != confirmPass) {
                Toast.makeText(context, "Las nuevas contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Llamada al ViewModel
            viewModel.cambiarPassword(currentPass, newPass, confirmPass)
        }

        // --- Listener Cerrar Sesión ---
        btnLogout.setOnClickListener {
            logout()
        }
    }

    private fun observeViewModel() {
        // Observar datos del usuario actual
        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            user?.let {
                etName.setText(it.nombre)
                etApellido.setText(it.apellido)
                etEmail.setText(it.email)
            }
        }

        // Observar estado de la actualización de DATOS
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

        // Observar estado del cambio de CONTRASEÑA
        viewModel.passwordState.observe(viewLifecycleOwner) { state ->
            when (state) {
                UpdateState.Loading -> {
                    btnChangePassword.isEnabled = false
                    btnChangePassword.text = "Cambiando..."
                }
                is UpdateState.Success -> {
                    btnChangePassword.isEnabled = true
                    btnChangePassword.text = "Cambiar Contraseña"
                    Toast.makeText(requireContext(), "✅ ${state.message}", Toast.LENGTH_LONG).show()

                    // Limpiar campos de contraseña tras éxito
                    etCurrentPassword.text?.clear()
                    etNewPassword.text?.clear()
                    etConfirmPassword.text?.clear()

                    viewModel.resetPasswordState()
                }
                is UpdateState.Error -> {
                    btnChangePassword.isEnabled = true
                    btnChangePassword.text = "Cambiar Contraseña"
                    // Muestra el error (ej: "Contraseña actual incorrecta")
                    Toast.makeText(requireContext(), "❌ ${state.message}", Toast.LENGTH_LONG).show()
                    viewModel.resetPasswordState()
                }
                UpdateState.Idle -> {
                    btnChangePassword.isEnabled = true
                    btnChangePassword.text = "Cambiar Contraseña"
                }
            }
        }
    }

    private fun logout() {
        sessionManager.clearSession()
        val intent = Intent(requireContext(), BienvenidaActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }
}
package com.intellisoft.androidcuisine.views.Main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.data.managers.SessionManager
import com.intellisoft.androidcuisine.data.remote.api.ApiClient
import com.intellisoft.androidcuisine.databinding.FragmentCuentaBinding
import kotlinx.coroutines.launch

class CuentaFragment : Fragment() {

    private var _binding: FragmentCuentaBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCuentaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sessionManager = SessionManager(requireContext())
        val userData = sessionManager.getUserData()
        val userId = userData?.id

        if (userId != null) {
            fetchUserData(userId)
        } else {
            Toast.makeText(requireContext(), "Usuario no autenticado", Toast.LENGTH_SHORT).show()
        }

        binding.btnUpdate.setOnClickListener {
            updateUserData(userId)
        }

        binding.btnChangePassword.setOnClickListener {
            changePassword()
        }

        binding.btnLogout.setOnClickListener {
            sessionManager.clearSession()
            Toast.makeText(requireContext(), "Sesión cerrada", Toast.LENGTH_SHORT).show()
            // Redirigir al login
        }
    }

    private fun fetchUserData(userId: Int) {
        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getUser(userId)
                if (response.isSuccessful) {
                    val user = response.body()?.data
                    binding.etName.setText(user?.nombre)
                    binding.etEmail.setText(user?.email)
                    binding.etPhone.setText(user?.telefono)
                } else {
                    Toast.makeText(requireContext(), "Error al obtener datos", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error de red", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUserData(userId: Int?) {
        if (userId == null) return

        val name = binding.etName.text.toString()
        val email = binding.etEmail.text.toString()
        val phone = binding.etPhone.text.toString()

        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.updateUser(userId, name, email, phone)
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Datos actualizados", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Error al actualizar", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error de red", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun changePassword() {
        val currentPassword = binding.etCurrentPassword.text.toString()
        val newPassword = binding.etNewPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()

        if (newPassword != confirmPassword) {
            Toast.makeText(requireContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.changePassword(currentPassword, newPassword)
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Contraseña actualizada", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Error al cambiar contraseña", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error de red", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

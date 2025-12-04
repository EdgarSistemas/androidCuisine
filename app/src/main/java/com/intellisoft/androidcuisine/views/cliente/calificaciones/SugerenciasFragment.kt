package com.intellisoft.androidcuisine.views.cliente.calificaciones

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.views.cliente.ClienteViewModel
import com.intellisoft.androidcuisine.views.cliente.CrearMejoraState

class SugerenciasFragment : Fragment() {

    private lateinit var viewModel: ClienteViewModel

    private lateinit var etSugerencia: TextInputEditText
    private lateinit var btnEnviar: MaterialButton
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sugerencias, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[ClienteViewModel::class.java]

        initViews(view)
        setupListeners()
        observeViewModel()
    }

    private fun initViews(view: View) {
        etSugerencia = view.findViewById(R.id.etSugerencia)
        btnEnviar = view.findViewById(R.id.btnEnviar)
        progressBar = view.findViewById(R.id.progressBar)
    }

    private fun setupListeners() {
        btnEnviar.setOnClickListener {
            enviarSugerencia()
        }
    }

    private fun observeViewModel() {
        viewModel.crearMejoraState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is CrearMejoraState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    btnEnviar.isEnabled = false
                }
                is CrearMejoraState.Success -> {
                    progressBar.visibility = View.GONE
                    btnEnviar.isEnabled = true
                    Toast.makeText(requireContext(), "¡Gracias por tu sugerencia! 💡", Toast.LENGTH_LONG).show()
                    etSugerencia.setText("")
                }
                is CrearMejoraState.Error -> {
                    progressBar.visibility = View.GONE
                    btnEnviar.isEnabled = true
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
                else -> {
                    progressBar.visibility = View.GONE
                    btnEnviar.isEnabled = true
                }
            }
        }
    }

    private fun enviarSugerencia() {
        val sugerencia = etSugerencia.text.toString().trim()

        if (sugerencia.isEmpty()) {
            etSugerencia.error = "Escribe tu sugerencia"
            return
        }

        if (sugerencia.length < 10) {
            etSugerencia.error = "La sugerencia es muy corta"
            return
        }

        viewModel.crearMejora(sugerencia)
    }
}
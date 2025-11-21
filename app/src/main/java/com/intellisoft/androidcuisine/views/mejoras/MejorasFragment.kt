package com.intellisoft.androidcuisine.views.mejoras

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.views.adapters.MejorasAdapter

class MejorasFragment : Fragment() {

    private val viewModel: MejorasViewModel by viewModels()

    // Vistas del formulario
    private lateinit var etMejora: TextInputEditText
    private lateinit var tilMejora: TextInputLayout
    private lateinit var btnEnviar: Button
    private lateinit var progressBar: ProgressBar

    // Vistas de la lista
    private lateinit var rvMejoras: RecyclerView
    private lateinit var tvEmptyList: TextView
    private val adapter = MejorasAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mejoras, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupRecyclerView()
        setupListeners()
        observeViewModel()

        // Cargar la lista al iniciar la pantalla
        viewModel.cargarMejoras()
    }

    private fun initViews(view: View) {
        // Formulario
        etMejora = view.findViewById(R.id.etMejora)
        tilMejora = view.findViewById(R.id.tilMejora)
        btnEnviar = view.findViewById(R.id.btnEnviar)
        progressBar = view.findViewById(R.id.progressBar)

        // Lista
        rvMejoras = view.findViewById(R.id.rvMejoras)
        tvEmptyList = view.findViewById(R.id.tvEmptyList)
    }

    private fun setupRecyclerView() {
        rvMejoras.layoutManager = LinearLayoutManager(requireContext())
        rvMejoras.adapter = adapter
    }

    private fun setupListeners() {
        btnEnviar.setOnClickListener {
            val texto = etMejora.text.toString().trim()

            if (texto.isEmpty()) {
                tilMejora.error = "Por favor escribe algo"
                return@setOnClickListener
            } else {
                tilMejora.error = null
            }

            viewModel.enviarSugerencia(texto)
        }
    }

    private fun observeViewModel() {
        // Observer para el envío (POST)
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is MejorasState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    btnEnviar.isEnabled = false
                    btnEnviar.text = "Enviando..."
                }
                is MejorasState.Success -> {
                    progressBar.visibility = View.GONE
                    btnEnviar.isEnabled = true
                    btnEnviar.text = "Enviar Sugerencia"
                    Toast.makeText(requireContext(), "✅ ${state.message}", Toast.LENGTH_LONG).show()

                    etMejora.text?.clear()
                    tilMejora.error = null
                    viewModel.resetState()
                }
                is MejorasState.Error -> {
                    progressBar.visibility = View.GONE
                    btnEnviar.isEnabled = true
                    btnEnviar.text = "Enviar Sugerencia"
                    Toast.makeText(requireContext(), "❌ ${state.message}", Toast.LENGTH_LONG).show()
                    viewModel.resetState()
                }
                is MejorasState.Idle -> {
                    progressBar.visibility = View.GONE
                    btnEnviar.isEnabled = true
                }
            }
        }

        // Observer para la lista (GET)
        viewModel.listState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is MejorasListState.Loading -> {
                    // Aquí podrías mostrar otro loader si quisieras, o usar el mismo
                }
                is MejorasListState.Success -> {
                    rvMejoras.visibility = View.VISIBLE
                    tvEmptyList.visibility = View.GONE
                    adapter.submitList(state.data)
                }
                is MejorasListState.Empty -> {
                    rvMejoras.visibility = View.GONE
                    tvEmptyList.visibility = View.VISIBLE
                }
                is MejorasListState.Error -> {
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
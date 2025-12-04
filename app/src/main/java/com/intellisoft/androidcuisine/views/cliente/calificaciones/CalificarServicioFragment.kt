package com.intellisoft.androidcuisine.views.cliente.calificaciones


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.views.cliente.ClienteViewModel
import com.intellisoft.androidcuisine.views.cliente.CrearCalificacionState

class CalificarServicioFragment : Fragment() {

    private lateinit var viewModel: ClienteViewModel

    private lateinit var ratingBar: RatingBar
    private lateinit var etComentario: TextInputEditText
    private lateinit var etPedidoId: TextInputEditText
    private lateinit var btnEnviar: MaterialButton
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_calificar_servicio, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[ClienteViewModel::class.java]

        initViews(view)
        setupListeners()
        observeViewModel()
    }

    private fun initViews(view: View) {
        ratingBar = view.findViewById(R.id.ratingBar)
        etComentario = view.findViewById(R.id.etComentario)
        etPedidoId = view.findViewById(R.id.etPedidoId)
        btnEnviar = view.findViewById(R.id.btnEnviar)
        progressBar = view.findViewById(R.id.progressBar)
    }

    private fun setupListeners() {
        btnEnviar.setOnClickListener {
            enviarCalificacion()
        }
    }

    private fun observeViewModel() {
        viewModel.crearCalificacionState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is CrearCalificacionState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    btnEnviar.isEnabled = false
                }
                is CrearCalificacionState.Success -> {
                    progressBar.visibility = View.GONE
                    btnEnviar.isEnabled = true
                    Toast.makeText(requireContext(), "¡Gracias por tu calificación! ⭐", Toast.LENGTH_LONG).show()
                    limpiarFormulario()
                }
                is CrearCalificacionState.Error -> {
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

    private fun enviarCalificacion() {
        val pedidoIdText = etPedidoId.text.toString().trim()
        val calificacion = ratingBar.rating.toInt()
        val comentario = etComentario.text.toString().trim()

        if (pedidoIdText.isEmpty()) {
            etPedidoId.error = "Ingresa el ID del pedido"
            return
        }

        val pedidoId = pedidoIdText.toIntOrNull()
        if (pedidoId == null) {
            etPedidoId.error = "ID inválido"
            return
        }

        if (calificacion == 0) {
            Toast.makeText(requireContext(), "Selecciona una calificación", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.crearCalificacion(
            pedidoId = pedidoId,
            calificacion = calificacion,
            notas = if (comentario.isNotEmpty()) comentario else null
        )
    }

    private fun limpiarFormulario() {
        etPedidoId.setText("")
        etComentario.setText("")
        ratingBar.rating = 0f
    }
}
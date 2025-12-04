package com.intellisoft.androidcuisine.views.cliente

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.card.MaterialCardView
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.views.cliente.seleccion.ReservasClienteFragment
import com.intellisoft.androidcuisine.views.cliente.pedidos.PedidosClienteFragment
import com.intellisoft.androidcuisine.views.cliente.pedidos.PedidoTakeawayFragment
import com.intellisoft.androidcuisine.views.cliente.cupones.MisCuponesFragment
import com.intellisoft.androidcuisine.views.cliente.calificaciones.CalificarServicioFragment
import com.intellisoft.androidcuisine.views.cliente.calificaciones.SugerenciasFragment
import com.intellisoft.androidcuisine.util.SessionManager

class ClienteHomeFragment : Fragment() {

    private lateinit var viewModel: ClienteViewModel
    private lateinit var sessionManager: SessionManager

    private lateinit var tvSaludo: TextView
    private lateinit var cardReservar: MaterialCardView
    private lateinit var cardPedidoTakeaway: MaterialCardView
    private lateinit var cardMisPedidos: MaterialCardView
    private lateinit var cardMisCupones: MaterialCardView
    private lateinit var cardCalificar: MaterialCardView
    private lateinit var cardSugerencias: MaterialCardView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cliente_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[ClienteViewModel::class.java]
        sessionManager = SessionManager(requireContext())
        viewModel.setClienteId(sessionManager.getUserData()?.id ?: 0)

        initViews(view)
        setupSaludo()
        setupClickListeners()
        setupClienteId()
    }

    private fun initViews(view: View) {
        tvSaludo = view.findViewById(R.id.tvSaludo)
        cardReservar = view.findViewById(R.id.cardReservar)
        cardPedidoTakeaway = view.findViewById(R.id.cardPedidoTakeaway)
        cardMisPedidos = view.findViewById(R.id.cardMisPedidos)
        cardMisCupones = view.findViewById(R.id.cardMisCupones)
        cardCalificar = view.findViewById(R.id.cardCalificar)
        cardSugerencias = view.findViewById(R.id.cardSugerencias)
    }

    private fun setupSaludo() {
        val nombre = sessionManager.getUserData()?.nombre ?: "Cliente"
        tvSaludo.text = "¡Hola, $nombre!"
    }

    private fun setupClienteId() {
        val clienteId = sessionManager.getUserData()?.id
        if (clienteId != null) {
            viewModel.setClienteId(clienteId)
        }
    }

    private fun setupClickListeners() {
        cardReservar.setOnClickListener {
            navigateTo(ReservasClienteFragment())
        }

        cardPedidoTakeaway.setOnClickListener {
            navigateTo(PedidoTakeawayFragment())
        }

        cardMisPedidos.setOnClickListener {
            navigateTo(PedidosClienteFragment())
        }

        cardMisCupones.setOnClickListener {
            navigateTo(MisCuponesFragment())
        }

        cardCalificar.setOnClickListener {
            navigateTo(CalificarServicioFragment())
        }

        cardSugerencias.setOnClickListener {
            navigateTo(SugerenciasFragment())
        }
    }

    private fun navigateTo(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}
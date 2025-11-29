package com.intellisoft.androidcuisine.views.marketing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView
import com.intellisoft.androidcuisine.R

class MetricasFragment : Fragment() {

    private lateinit var cardVIP: MaterialCardView
    private lateinit var cardFrecuentes: MaterialCardView
    private lateinit var cardInactivos: MaterialCardView
    private lateinit var cardNuevos: MaterialCardView
    private lateinit var cardCanal: MaterialCardView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_metricas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews(view)
        setupListeners()
    }

    private fun setupViews(view: View) {
        cardVIP = view.findViewById(R.id.cardVIP)
        cardFrecuentes = view.findViewById(R.id.cardFrecuentes)
        cardInactivos = view.findViewById(R.id.cardInactivos)
        cardNuevos = view.findViewById(R.id.cardNuevos)
        cardCanal = view.findViewById(R.id.cardCanal)
    }

    private fun setupListeners() {
        cardVIP.setOnClickListener {
            navigateToMetrica("vip")
        }

        cardFrecuentes.setOnClickListener {
            navigateToMetrica("frecuentes")
        }

        cardInactivos.setOnClickListener {
            navigateToMetrica("inactivos")
        }

        cardNuevos.setOnClickListener {
            navigateToMetrica("nuevos")
        }

        cardCanal.setOnClickListener {
            navigateToMetrica("canal")
        }
    }

    private fun navigateToMetrica(tipo: String) {
        val fragment = when (tipo) {
            "vip" -> ClientesVIPFragment()
            "frecuentes" -> ClientesFrecuentesFragment()
            "inactivos" -> ClientesInactivosFragment()
            "nuevos" -> ClientesNuevosFragment()
            "canal" -> ClientesCanalFragment()
            else -> return
        }

        // Usar requireActivity() para acceder al FragmentManager del Activity
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}
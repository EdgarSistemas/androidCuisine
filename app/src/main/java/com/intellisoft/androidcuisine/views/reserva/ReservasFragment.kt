package com.intellisoft.androidcuisine.views.reserva

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.util.SessionManager

class ReservasFragment : Fragment() {

    private lateinit var viewModel: ReservasViewModel
    private lateinit var sessionManager: SessionManager

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: androidx.viewpager2.widget.ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_reservas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[ReservasViewModel::class.java]
        sessionManager = SessionManager(requireContext())

        // Configurar el actor según el rol del usuario
        val usuarioId = sessionManager.getUserId()
        val userData = sessionManager.getUserData()

        // Usar es_cliente del login para determinar el tipo de actor
        val tipoActor = if (userData?.esCliente == true) {
            TipoActor.CLIENTE
        } else {
            TipoActor.RECEPCIONISTA
        }

        viewModel.configurarActor(tipoActor, usuarioId)

        tabLayout = view.findViewById(R.id.tabLayout)
        viewPager = view.findViewById(R.id.viewPager)

        setupViewPager()
    }

    private fun setupViewPager() {
        val adapter = ReservasPagerAdapter(this)
        viewPager.adapter = adapter

        val esCliente = viewModel.esCliente()

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> if (esCliente) "Mis Reservas" else "Reservas"
                1 -> "Nueva"
                else -> ""
            }
        }.attach()
    }

    private inner class ReservasPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> ReservasListaFragment()
                1 -> NuevaReservaFragment()
                else -> ReservasListaFragment()
            }
        }
    }
}
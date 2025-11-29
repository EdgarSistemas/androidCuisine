package com.intellisoft.androidcuisine.views.reserva

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.util.SessionManager
import com.intellisoft.androidcuisine.views.reserva.ReservasClienteViewModel

class PedidosClienteFragment : Fragment() {

    private lateinit var viewModel: PedidosClienteViewModel
    private lateinit var sessionManager: SessionManager

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pedidos_cliente, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[PedidosClienteViewModel::class.java]
        sessionManager = SessionManager(requireContext())

        // Obtener sucursal del ViewModel de reservas si existe
        val reservasViewModel = ViewModelProvider(requireActivity())[ReservasClienteViewModel::class.java]
        val sucursalId = reservasViewModel.getSucursalValue()?.id_sucursal

        viewModel.configurarCliente(sessionManager.getUserId(), sucursalId)

        setupViews(view)
        setupViewPager()
    }

    private fun setupViews(view: View) {
        tabLayout = view.findViewById(R.id.tabLayout)
        viewPager = view.findViewById(R.id.viewPager)
    }

    private fun setupViewPager() {
        val adapter = PedidosPagerAdapter(this)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Mis Pedidos"
                1 -> "Para Llevar"
                else -> ""
            }
        }.attach()
    }

    fun irANuevoPedido() {
        viewPager.currentItem = 1
    }

    fun irAMisPedidos() {
        viewPager.currentItem = 0
    }

    private inner class PedidosPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> MisPedidosClienteFragment()
                1 -> NuevoPedidoTakeawayFragment()
                else -> MisPedidosClienteFragment()
            }
        }
    }
}
package com.intellisoft.androidcuisine.views.horario

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.intellisoft.androidcuisine.R

class HorariosFragment : Fragment() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: androidx.viewpager2.widget.ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        android.util.Log.d("HorariosFragment", "onCreateView ejecutado")
        val view = inflater.inflate(R.layout.fragment_horarios, container, false)
        android.util.Log.d("HorariosFragment", "View inflado: $view")
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        android.widget.Toast.makeText(requireContext(), "HorariosFragment cargado", android.widget.Toast.LENGTH_SHORT).show()

        tabLayout = view.findViewById(R.id.tabLayout)
        viewPager = view.findViewById(R.id.viewPager)

        setupViewPager()
    }

    private fun setupViewPager() {
        val adapter = HorariosPagerAdapter(this)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Mi Horario"
                1 -> "Solicitudes"
                else -> ""
            }
        }.attach()
    }

    private inner class HorariosPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> MiHorarioFragment()
                1 -> SolicitudesFragment()
                else -> MiHorarioFragment()
            }
        }
    }
}
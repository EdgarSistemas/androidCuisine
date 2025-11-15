package com.intellisoft.androidcuisine.views.horario

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.data.remote.dto.HorarioDetalle
import com.intellisoft.androidcuisine.views.adapters.HorarioAdapter

class MiHorarioFragment : Fragment() {

    private lateinit var viewModel: HorariosViewModel

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView
    private lateinit var adapter: HorarioAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mi_horario, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[HorariosViewModel::class.java]

        setupViews(view)
        setupRecyclerView()
        observeViewModel()

        viewModel.loadHorarioUsuario()
    }

    private fun setupViews(view: View) {
        recyclerView = view.findViewById(R.id.recyclerViewHorarios)
        progressBar = view.findViewById(R.id.progressBarHorarios)
        tvEmpty = view.findViewById(R.id.tvHorarioEmpty)
    }

    private fun setupRecyclerView() {
        adapter = HorarioAdapter(
            onRegistrarClick = { detalle ->
                showRegistrarAsistenciaDialog(detalle)
            }
        )
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.horarioState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is HorarioState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                    tvEmpty.visibility = View.GONE
                }
                is HorarioState.Success -> {
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    tvEmpty.visibility = View.GONE
                    adapter.updateData(state.detalles)
                }
                is HorarioState.Empty -> {
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                    tvEmpty.visibility = View.VISIBLE
                }
                is HorarioState.Error -> {
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                    tvEmpty.visibility = View.VISIBLE
                    tvEmpty.text = state.message
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun showRegistrarAsistenciaDialog(detalle: HorarioDetalle) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_registrar_asistencia, null)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .create()

        dialogView.findViewById<View>(R.id.btnCancelar).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<View>(R.id.btnConfirmar).setOnClickListener {
            // TODO: Implementar lógica de registro
            Toast.makeText(requireContext(), "Asistencia registrada", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.show()
    }
}
package com.intellisoft.androidcuisine.views.marketing

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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.views.adapters.CampaniasAdapter

class CampaniasFragment : Fragment() {

    private lateinit var viewModel: MarketingViewModel

    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView

    private lateinit var adapter: CampaniasAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_campanias, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[MarketingViewModel::class.java]

        setupViews(view)
        setupRecyclerView()
        setupListeners()
        observeViewModel()

        viewModel.loadCampanias()
    }

    private fun setupViews(view: View) {
        recyclerView = view.findViewById(R.id.recyclerViewCampanias)
        swipeRefresh = view.findViewById(R.id.swipeRefresh)
        progressBar = view.findViewById(R.id.progressBar)
        tvEmpty = view.findViewById(R.id.tvEmpty)

    }

    private fun setupRecyclerView() {
        adapter = CampaniasAdapter(
            onSwitchChanged = { campania, isChecked ->
                if (isChecked) {
                    viewModel.activarCampania(campania.id_campania)
                } else {
                    viewModel.desactivarCampania(campania.id_campania)
                }
            }
        )
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun setupListeners() {
        swipeRefresh.setOnRefreshListener {
            viewModel.loadCampanias()
        }
    }

    private fun observeViewModel() {
        viewModel.campaniasState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is CampaniasState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                    tvEmpty.visibility = View.GONE
                }
                is CampaniasState.Success -> {
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    tvEmpty.visibility = View.GONE
                    adapter.submitList(state.campanias)
                    swipeRefresh.isRefreshing = false
                }
                is CampaniasState.Empty -> {
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                    tvEmpty.visibility = View.VISIBLE
                    swipeRefresh.isRefreshing = false
                }
                is CampaniasState.Error -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    swipeRefresh.isRefreshing = false
                }
            }
        }
    }
}
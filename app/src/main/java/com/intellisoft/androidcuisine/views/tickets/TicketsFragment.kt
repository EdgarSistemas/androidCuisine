package com.intellisoft.androidcuisine.views.tickets

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.data.remote.dto.TicketDto
import com.intellisoft.androidcuisine.views.adapters.TicketsAdapter

class TicketsFragment : Fragment() {

    private val viewModel: TicketsViewModel by viewModels()
    private lateinit var adapter: TicketsAdapter

    // UI Components
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView
    private lateinit var fabAdd: FloatingActionButton

    // Variables para captura de foto
    private var currentPhotoBitmap: Bitmap? = null
    private lateinit var ivDialogPreview: ImageView // Referencia a la vista del dialog actual
    private lateinit var btnDialogFoto: Button

    // Launcher de Cámara (Thumbnail)
    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            currentPhotoBitmap = bitmap
            if (::ivDialogPreview.isInitialized) {
                ivDialogPreview.setImageBitmap(bitmap)
                ivDialogPreview.visibility = View.VISIBLE
                btnDialogFoto.text = "Retomar Foto"
            }
        }
    }

    // Launcher de Permisos
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            abrirCamara()
        } else {
            Toast.makeText(context, "Se requiere permiso de cámara", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_tickets, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupRecyclerView()
        setupObservers()

        fabAdd.setOnClickListener { mostrarDialogoCrearTicket() }

        viewModel.cargarTickets()
    }

    private fun initViews(view: View) {
        recyclerView = view.findViewById(R.id.rvTickets)
        progressBar = view.findViewById(R.id.pbTickets)
        tvEmpty = view.findViewById(R.id.tvTicketsEmpty)
        fabAdd = view.findViewById(R.id.fabNewTicket)
    }

    private fun setupRecyclerView() {
        adapter = TicketsAdapter { ticket ->
            mostrarImagenCompleta(ticket)
        }
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.ticketsState.observe(viewLifecycleOwner) { state ->
            when(state) {
                is TicketsListState.Loading -> progressBar.visibility = View.VISIBLE
                is TicketsListState.Success -> {
                    progressBar.visibility = View.GONE
                    tvEmpty.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    adapter.submitList(state.data)
                }
                is TicketsListState.Empty -> {
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                    tvEmpty.visibility = View.VISIBLE
                }
                is TicketsListState.Error -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        viewModel.createState.observe(viewLifecycleOwner) { state ->
            // Manejado dentro del diálogo idealmente, o aquí globalmente
            if (state is TicketCreateState.Success) {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                viewModel.resetCreateState()
            } else if (state is TicketCreateState.Error) {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                viewModel.resetCreateState()
            }
        }
    }

    private fun mostrarDialogoCrearTicket() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_crear_ticket, null)
        val etNotas = dialogView.findViewById<EditText>(R.id.etTicketNotas)
        ivDialogPreview = dialogView.findViewById(R.id.ivTicketPreview)
        btnDialogFoto = dialogView.findViewById(R.id.btnTomarFoto)

        currentPhotoBitmap = null // Resetear

        btnDialogFoto.setOnClickListener {
            verificarPermisosYAbirCamara()
        }

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Nuevo Ticket")
            .setView(dialogView)
            .setCancelable(false)
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Enviar", null) // Se configura después para evitar cierre automático
            .create()

        dialog.setOnShowListener {
            val button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            button.setOnClickListener {
                val notas = etNotas.text.toString()
                if (notas.isBlank() || currentPhotoBitmap == null) {
                    Toast.makeText(context, "Falta descripción o foto", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.crearTicket(notas, currentPhotoBitmap)
                    dialog.dismiss()
                }
            }
        }
        dialog.show()
    }

    private fun verificarPermisosYAbirCamara() {
        when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                abrirCamara()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                Toast.makeText(context, "Necesitamos la cámara para evidenciar el ticket", Toast.LENGTH_LONG).show()
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun abrirCamara() {
        cameraLauncher.launch(null)
    }

    private fun mostrarImagenCompleta(ticket: TicketDto) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_ver_imagen, null)
        val ivFull = dialogView.findViewById<ImageView>(R.id.ivFullImage)
        val tvDesc = dialogView.findViewById<TextView>(R.id.tvFullDescription)

        tvDesc.text = ticket.notas

        if (!ticket.imagenUrl.isNullOrEmpty()) {
            if (ticket.imagenUrl.startsWith("http")) {
                ivFull.load(ticket.imagenUrl)
            } else {
                try {
                    val decodedString = Base64.decode(ticket.imagenUrl, Base64.DEFAULT)
                    val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                    ivFull.setImageBitmap(decodedByte)
                } catch (e: Exception) { }
            }
        }

        MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .setPositiveButton("Cerrar", null)
            .show()
    }
}
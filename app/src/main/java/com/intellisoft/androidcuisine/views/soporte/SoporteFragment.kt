package com.intellisoft.androidcuisine.views.soporte

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.views.adapters.ChatAdapter
import kotlinx.coroutines.launch

class SoporteFragment : Fragment() {

    private val viewModel: SoporteViewModel by viewModels()
    private lateinit var adapter: ChatAdapter
    private lateinit var rvChat: RecyclerView
    private lateinit var etMessage: EditText
    private lateinit var btnSend: FloatingActionButton
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_soporte, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupRecyclerView()

        // Mensaje de bienvenida
        adapter.addMessage(ChatMessage("¡Hola! Soy el asistente de Android Cuisine. ¿En qué puedo ayudarte con la app?", false))

        btnSend.setOnClickListener {
            sendMessage()
        }

        // Observar estado de carga
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                btnSend.isEnabled = !isLoading
            }
        }
    }

    private fun initViews(view: View) {
        rvChat = view.findViewById(R.id.rvChat)
        etMessage = view.findViewById(R.id.etMessage)
        btnSend = view.findViewById(R.id.btnSend)
        progressBar = view.findViewById(R.id.progressBarChat)
    }

    private fun setupRecyclerView() {
        adapter = ChatAdapter()
        rvChat.layoutManager = LinearLayoutManager(context).apply {
            stackFromEnd = true // Comenzar desde abajo
        }
        rvChat.adapter = adapter
    }

    private fun sendMessage() {
        val text = etMessage.text.toString().trim()
        if (text.isNotEmpty()) {
            // 1. Agregar mensaje del usuario a la UI
            adapter.addMessage(ChatMessage(text, true))
            etMessage.text.clear()
            rvChat.smoothScrollToPosition(adapter.itemCount - 1)

            // 2. Llamar a la API
            viewModel.sendMessage(
                userMessage = text,
                onResponse = { response ->
                    // 3. Agregar respuesta del bot
                    adapter.addMessage(ChatMessage(response, false))
                    rvChat.smoothScrollToPosition(adapter.itemCount - 1)
                },
                onError = { error ->
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                    adapter.addMessage(ChatMessage("⚠️ Ocurrió un error al conectar con el servidor.", false))
                }
            )
        }
    }
}
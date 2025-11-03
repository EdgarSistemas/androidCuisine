package com.intellisoft.androidcuisine.views.Main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.intellisoft.androidcuisine.R

/**
 * Un Fragment de ejemplo para la pantalla de Inicio.
 */
class InicioFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla el layout para este fragment
        val view = inflater.inflate(R.layout.fragment_inicio, container, false)

        // Puedes configurar la vista aquí, por ejemplo:
        val textView: TextView = view.findViewById(R.id.text_inicio)
        textView.text = "¡Bienvenido a Android Cuisine!"

        return view
    }
}

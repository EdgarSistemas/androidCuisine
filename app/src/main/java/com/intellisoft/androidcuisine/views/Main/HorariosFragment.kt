package com.intellisoft.androidcuisine.views.Main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.intellisoft.androidcuisine.R

class HorariosFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Esta línea infla (carga) el nuevo layout XML que creaste en el Paso 1
        return inflater.inflate(R.layout.fragment_horarios, container, false)
    }
}
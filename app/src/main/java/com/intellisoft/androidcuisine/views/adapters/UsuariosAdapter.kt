package com.intellisoft.androidcuisine.views.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.intellisoft.androidcuisine.data.remote.dto.UsuarioDto
import com.intellisoft.androidcuisine.databinding.ItemUsuarioBinding

class UsuariosAdapter(
    private val onEditClick: (UsuarioDto) -> Unit,
    private val onDeleteClick: (UsuarioDto) -> Unit
) : ListAdapter<UsuarioDto, UsuariosAdapter.UsuarioViewHolder>(UsuarioDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val binding = ItemUsuarioBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UsuarioViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class UsuarioViewHolder(private val binding: ItemUsuarioBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(usuario: UsuarioDto) {
            // Ajusta estos textos según tu item_usuario.xml
            binding.tvNombreUsuario.text = "${usuario.nombre} ${usuario.apellido ?: ""}"
            binding.tvEmailUsuario.text = usuario.email
            binding.tvRolUsuario.text = usuario.roles?.firstOrNull()?.nombre ?: "Sin Rol"

            // Botones de acción
            binding.btnEditar.setOnClickListener { onEditClick(usuario) }
            binding.btnEliminar.setOnClickListener { onDeleteClick(usuario) }
        }
    }

    class UsuarioDiffCallback : DiffUtil.ItemCallback<UsuarioDto>() {
        override fun areItemsTheSame(oldItem: UsuarioDto, newItem: UsuarioDto): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: UsuarioDto, newItem: UsuarioDto): Boolean = oldItem == newItem
    }
}
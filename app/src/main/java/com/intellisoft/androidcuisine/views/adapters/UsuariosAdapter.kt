package com.intellisoft.androidcuisine.views.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.intellisoft.androidcuisine.data.remote.dto.UsuarioDto
import com.intellisoft.androidcuisine.databinding.ItemUsuarioBinding

class UsuariosAdapter(
    private var usuarios: List<UsuarioDto>,
    private val onEditClick: (UsuarioDto) -> Unit,
    private val onDeleteClick: (UsuarioDto) -> Unit
) : RecyclerView.Adapter<UsuariosAdapter.UsuarioViewHolder>() {

    fun updateList(newUsuarios: List<UsuarioDto>) {
        usuarios = newUsuarios
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val binding = ItemUsuarioBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UsuarioViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        holder.bind(usuarios[position])
    }

    override fun getItemCount() = usuarios.size

    inner class UsuarioViewHolder(private val binding: ItemUsuarioBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(usuario: UsuarioDto) {
            binding.tvNombreCompleto.text = "${usuario.nombre} ${usuario.apellido}"
            binding.tvEmail.text = usuario.email

            // Si tu API devuelve el rol anidado, úsalo. Si solo devuelve ID, puedes poner un placeholder o mapearlo
            binding.chipRol.text = "Rol ID: ${usuario.rolId}"

            binding.btnEditar.setOnClickListener { onEditClick(usuario) }
            binding.btnEliminar.setOnClickListener { onDeleteClick(usuario) }
        }
    }
}
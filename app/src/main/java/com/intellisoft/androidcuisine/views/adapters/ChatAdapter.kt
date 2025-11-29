package com.intellisoft.androidcuisine.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.views.soporte.ChatMessage

class ChatAdapter : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    private val messages = mutableListOf<ChatMessage>()

    fun addMessage(message: ChatMessage) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_message, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount(): Int = messages.size

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardBot: MaterialCardView = itemView.findViewById(R.id.cardBot)
        private val tvMessageBot: TextView = itemView.findViewById(R.id.tvMessageBot)
        private val cardUser: MaterialCardView = itemView.findViewById(R.id.cardUser)
        private val tvMessageUser: TextView = itemView.findViewById(R.id.tvMessageUser)

        fun bind(chatMessage: ChatMessage) {
            if (chatMessage.isUser) {
                // Mostrar diseño de usuario, ocultar bot
                cardUser.visibility = View.VISIBLE
                cardBot.visibility = View.GONE
                tvMessageUser.text = chatMessage.message
            } else {
                // Mostrar diseño de bot, ocultar usuario
                cardUser.visibility = View.GONE
                cardBot.visibility = View.VISIBLE
                tvMessageBot.text = chatMessage.message
            }
        }
    }
}
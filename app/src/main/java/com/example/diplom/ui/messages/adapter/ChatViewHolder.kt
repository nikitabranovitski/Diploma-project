package com.example.diplom.ui.messages.adapter

import com.example.diplom.databinding.ChatListItemBinding
import com.example.diplom.model.Chat
import androidx.recyclerview.widget.RecyclerView

class ChatViewHolder (
    private val binding: ChatListItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Chat) {
        binding.itemTextView.text = item.message
    }
}
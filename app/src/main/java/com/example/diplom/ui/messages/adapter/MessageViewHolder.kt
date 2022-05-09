package com.example.diplom.ui.messages.adapter

import androidx.recyclerview.widget.RecyclerView
import com.example.diplom.databinding.MessageListItemBinding
import com.example.diplom.model.Message

class MessageViewHolder(
    private val binding: MessageListItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Message) {
        binding.itemTextView.text = item.firstEmail
        binding.secondEmailTextView.text = item.secondEmail
    }
}
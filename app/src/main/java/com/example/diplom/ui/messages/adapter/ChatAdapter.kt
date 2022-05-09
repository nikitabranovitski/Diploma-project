package com.example.diplom.ui.messages.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.diplom.databinding.ChatListItemBinding
import com.example.diplom.model.Chat

class ChatAdapter(
    private val context: Context,
) : RecyclerView.Adapter<ChatViewHolder>() {

    private var list = arrayListOf<Chat>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return ChatViewHolder(
            ChatListItemBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size

    @SuppressLint("NotifyDataSetChanged")
    fun setDataList(data: ArrayList<Chat>) {
        list = data
        notifyDataSetChanged()
    }
}
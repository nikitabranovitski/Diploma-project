package com.example.diplom.ui.profile.requests.adapter

import androidx.recyclerview.widget.RecyclerView
import com.example.diplom.databinding.ItemRequestBinding
import com.example.diplom.model.FriendRequest

class FriendRequestViewHolder(
    private val binding: ItemRequestBinding,
    var action: (item: FriendRequest) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: FriendRequest) {
        binding.apply {
            this.email.text = item.secondEmail
            this.acceptButton.setOnClickListener {
                action(item)
            }
        }
    }
}
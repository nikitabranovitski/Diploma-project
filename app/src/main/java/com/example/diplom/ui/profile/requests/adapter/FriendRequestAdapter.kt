package com.example.diplom.ui.profile.requests.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.diplom.databinding.ItemRequestBinding
import com.example.diplom.model.FriendRequest

class FriendRequestAdapter(
    private val context: Context,
    private val onClick: (item: FriendRequest) -> Unit
) : RecyclerView.Adapter<FriendRequestViewHolder>() {

    var listFriends = ArrayList<FriendRequest>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendRequestViewHolder {
        val binding = ItemRequestBinding.inflate(LayoutInflater.from(context))
        return FriendRequestViewHolder(binding, onClick).apply {
            setIsRecyclable(false)
        }
    }

    override fun getItemCount() = listFriends.size

    override fun onBindViewHolder(holder: FriendRequestViewHolder, position: Int) {
        holder.action = {
            onClick(it)
            notifyDataSetChanged()
        }
        holder.bind(listFriends[position])
    }

    fun setList(list: ArrayList<FriendRequest>) {
        listFriends = list
        notifyDataSetChanged()
    }
}
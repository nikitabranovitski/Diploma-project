package com.example.diplom.ui.messages.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.diplom.databinding.ItemPhotoUserBinding
import com.example.diplom.databinding.ListItemAllPhotoBinding
import com.example.diplom.databinding.MessageListItemBinding
import com.example.diplom.model.GalleryPhotoItem
import com.example.diplom.model.Message
import com.example.diplom.ui.allPhoto.adapter.AllPhotoViewHolder
import com.example.diplom.ui.profile.adapter.MyProfileViewHolder

class MessageAdapter(
    private val context: Context,
    val onClick: () -> Unit
) : RecyclerView.Adapter<MessageViewHolder>() {

    private var list = arrayListOf<Message>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return MessageViewHolder(
            MessageListItemBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(list[position])
        holder.itemView.setOnClickListener {
            onClick()
        }
    }

    override fun getItemCount() = list.size

}
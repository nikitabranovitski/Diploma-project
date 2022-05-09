package com.example.diplom.ui.profile.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.diplom.databinding.ItemPhotoUserBinding
import com.example.diplom.model.GalleryPhotoItem

class MyProfileAdapter(
    private val context: Context,
    val onClick: (item: GalleryPhotoItem) -> Unit
) : RecyclerView.Adapter<MyProfileViewHolder>() {

    private var list = arrayListOf<GalleryPhotoItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyProfileViewHolder {
        val binding = ItemPhotoUserBinding.inflate(LayoutInflater.from(context))
        return MyProfileViewHolder(binding).apply {
            setIsRecyclable(false)
        }
    }

    override fun onBindViewHolder(holder: MyProfileViewHolder, position: Int) {
        holder.bind(list[position])
        holder.itemView.setOnClickListener {
            onClick(list[position])
        }
    }

    override fun getItemCount() = list.size

    fun setDataList(data: ArrayList<GalleryPhotoItem>) {
        list = data
        notifyDataSetChanged()
    }
}
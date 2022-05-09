package com.example.diplom.ui.profile.adapter

import androidx.recyclerview.widget.RecyclerView
import com.example.diplom.databinding.ItemPhotoUserBinding
import com.example.diplom.model.GalleryPhotoItem
import com.example.diplom.util.loadPhotoGlideApp

class MyProfileViewHolder (
    private val binding: ItemPhotoUserBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(photo: GalleryPhotoItem) {

        photo.URLPhoto.let { binding.userPhotoImage.loadPhotoGlideApp(it) }

    }
}
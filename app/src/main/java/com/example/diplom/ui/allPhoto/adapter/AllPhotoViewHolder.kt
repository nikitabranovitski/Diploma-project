package com.example.diplom.ui.allPhoto.adapter

import android.annotation.SuppressLint
import android.graphics.drawable.AnimationDrawable
import android.view.animation.Animation
import androidx.recyclerview.widget.RecyclerView
import com.example.diplom.R
import com.example.diplom.databinding.ListItemAllPhotoBinding
import com.example.diplom.model.*
import com.example.diplom.model.AllPhoto.allPhotoList
import com.example.diplom.util.loadPhotoGlideApp
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AllPhotoViewHolder(
    private val binding: ListItemAllPhotoBinding,
    private var checkLike: (list: ArrayList<String>) -> Boolean
) : RecyclerView.ViewHolder(binding.root) {

    private lateinit var rocketAnimation: AnimationDrawable
    private var tempList = arrayListOf<String>()

    var image: Int = 0

    lateinit var clickListener: (GalleryPhotoItem, ArrayList<String>) -> Unit
    lateinit var delClickListener: (GalleryPhotoItem, ArrayList<String>) -> Unit
    val list = arrayListOf<String>()

    @SuppressLint("SetTextI18n")
    fun bind(photo: GalleryPhotoItem) {
        allPhotoList.forEach {
            tempList.add(it.id.toString())
        }
        photo.URLPhoto.let { binding.photoImageView.loadPhotoGlideApp(it) }
        allPhotoList.forEach { item ->
            if (photo.URLPhoto.contains(item.id!!)) {
                binding.priceTextView.text = "price" + item.price + "BYN"
                binding.locationTextView.text = "mall - " + item.nameMall
            }
        }

        var currentLikes = 0
        for (it in AllPhoto.tempList) {
            if (photo.URLPhoto.substringAfterLast("|") == it.idImage) {
                currentLikes = it.listUsersLike.size
                binding.countLikeTextView.text = currentLikes.toString()
                list.addAll(it.listUsersLike)

                break
            } else {
                binding.countLikeTextView.text = "0"
            }
        }

        binding.saveImageView.apply {
            if (checkLike(list)) {
                setBackgroundResource(R.drawable.animation_like_off)
                image = R.drawable.animation_like_off
            } else {
                setBackgroundResource(R.drawable.animation_like)
                image = R.drawable.animation_like
            }
            rocketAnimation = background as AnimationDrawable
        }.setOnClickListener {
            if (!checkLike(list)) {
                clickListener(photo, list)
                it.setBackgroundResource(R.drawable.animation_like_off)
                binding.countLikeTextView.text = (++currentLikes).toString()
            } else {
                it.setBackgroundResource(R.drawable.animation_like)
                delClickListener(photo, list)
                binding.countLikeTextView.text = (--currentLikes).toString()
            }
            rocketAnimation.start()
            GlobalScope.launch {
                delay(500)
                rocketAnimation.stop()
            }
        }
    }
}



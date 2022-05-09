package com.example.diplom.ui.allPhoto.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.diplom.databinding.ListItemAllPhotoBinding
import com.example.diplom.model.AllPhoto
import com.example.diplom.model.GalleryPhotoItem
import com.example.diplom.model.PhotoLike

class AllPhotoAdapter(
    private val context: Context,
    private var onCLick:(list:ArrayList<String>)->Boolean
) : RecyclerView.Adapter<AllPhotoViewHolder>() {

    private var list = arrayListOf<GalleryPhotoItem>()

    lateinit var clickListener: (GalleryPhotoItem, ArrayList<String>) -> Unit
    lateinit var delClickListener: (GalleryPhotoItem, ArrayList<String>) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllPhotoViewHolder {
        return AllPhotoViewHolder(
            ListItemAllPhotoBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        ){
            onCLick(it)
        }
    }

    override fun onBindViewHolder(holder: AllPhotoViewHolder, position: Int) {
        holder.bind(list[position])
        holder.clickListener = {it, list ->
            clickListener(it, list)
        }

        holder.delClickListener = {it, list ->
            delClickListener(it, list)
        }
    }

    override fun getItemCount() = list.size

    @SuppressLint("NotifyDataSetChanged")
    fun setDataList(data: ArrayList<GalleryPhotoItem>) {
        list = data
        notifyDataSetChanged()
    }
}
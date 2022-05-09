package com.example.diplom.util

import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import java.util.regex.Pattern
import com.example.diplom.model.GalleryPhotoItem
import com.example.diplom.modul.GlideApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

fun ImageView.loadUrl(url: String) {
    Glide.with(context).load(url).into(this)
}

val passwordPattern: Pattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6,}\$")
val pricePattern: Pattern = Pattern.compile("(.*\\d+)([.]?)")

var photoItem: GalleryPhotoItem? = null

fun ImageView.loadPhotoGlideApp(urlImage: String) {
    val imageRef = Firebase.storage.reference.child(urlImage)
    imageRef.downloadUrl.addOnSuccessListener { Uri ->
        val imageURL = Uri.toString()
        GlideApp.with(this)
            .load(imageURL)
            .into(this)
    }
}

var FLAG = false




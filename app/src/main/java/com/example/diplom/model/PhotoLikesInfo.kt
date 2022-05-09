package com.example.diplom.model

import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class PhotoLikesInfo(
    val idImage: String = "",
    var listUsersLike: ArrayList<String> = arrayListOf()
): Serializable
package com.example.diplom.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Mall(
    val id:String = "",
    val adress: String = "",
    val contactPhone: String = "",
    val email: String = "",
    val facebook: String = "",
    val instagram: String = "",
    val lat: String = "",
    val lon: String = "",
    val name: String = "",
    val vk: String = "",
    val webSite: String = ""
): Parcelable
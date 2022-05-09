package com.example.diplom.ui.profile.photoInfo

import androidx.lifecycle.ViewModel
import com.example.diplom.model.GalleryPhotoItem
import com.example.diplom.model.SavePhotoItem
import com.example.diplom.repository.FirebaseRepository
import com.example.diplom.repository.PhotoUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PhotoInfoViewModel @Inject constructor() :
    ViewModel() {

    lateinit var entry: (bool: Boolean) -> Unit
    lateinit var showProgressBar: (isShow: Boolean) -> Unit


    fun deletePhoto(item: SavePhotoItem, path: String) {
        showProgressBar(true)
        PhotoUserRepository.deletePhotoFromDatabase(item, path)
        PhotoUserRepository.delete = { it ->
            if (it) {
                entry(true)
                showProgressBar(false)
            } else {
                entry(false)
                showProgressBar(false)
            }
        }
    }

}
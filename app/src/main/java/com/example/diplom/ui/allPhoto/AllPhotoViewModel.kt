package com.example.diplom.ui.allPhoto

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.diplom.model.AllPhoto.allPhotoList
import com.example.diplom.model.GalleryPhotoItem
import com.example.diplom.model.PhotoLikesInfo
import com.example.diplom.model.SavePhotoItem
import com.example.diplom.repository.FirebaseRepository
import com.example.diplom.repository.PhotoUserRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AllPhotoViewModel @Inject constructor(private var repository: FirebaseRepository) :
    ViewModel() {

    private var auth = FirebaseAuth.getInstance()
    val list = MutableLiveData<ArrayList<GalleryPhotoItem>>()
    val listPhotoItemRTDB = MutableLiveData<ArrayList<PhotoLikesInfo>>()
    val listOfAllPhoto = MutableLiveData<ArrayList<SavePhotoItem>>()


    private fun getPhoto() {
        PhotoUserRepository.getAllPhotoFromFirebase()
        PhotoUserRepository.allPhoto.observeForever {
            listOfAllPhoto.postValue(PhotoUserRepository.allPhoto.value)
        }

        listOfAllPhoto.observeForever {
            allPhotoList.addAll(it)
        }
    }

    fun setLike(photo: String, list: ArrayList<String>){
        repository.setPhotoFromDBRealTimeTest(photo, list)
    }

    fun delLike(photo: String, list: ArrayList<String>){
        repository.delPhotoFromDBRealTimeTest(photo, list)
    }

    fun getCurrentUserEmail() = auth.currentUser?.email

    fun loadPhoto() {
        getPhoto()
        PhotoUserRepository.loadPhotoAll()
        PhotoUserRepository.allPhotoList.observeForever {
            list.postValue(PhotoUserRepository.allPhotoList.value)
        }
    }

    fun getDataFromDB() {
        repository.getDataFromDB()
        repository.dataList.observeForever {
            listPhotoItemRTDB.postValue(repository.dataList.value)
        }
    }
}
package com.example.diplom.ui.profile.savephoto

import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.diplom.model.Mall
import com.example.diplom.model.PhotoLikesInfo
import com.example.diplom.model.SavePhotoItem
import com.example.diplom.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.disposables.Disposable
import javax.inject.Inject

@HiltViewModel
class SavePhotoViewModel @Inject constructor(
    private var repository: FirebaseRepository,
) :
    ViewModel() {

    lateinit var entry: (bool: Boolean) -> Unit
    lateinit var showProgressBar: (isShow: Boolean) -> Unit
    private var disposable: Disposable? = null
    val listMallsName = MutableLiveData<ArrayList<Mall>>()

    override fun onCleared() {
        super.onCleared()
        disposable?.dispose()
    }

    private fun loadImagePhotoInfo(image: String, location: String) {
        repository.loadImagePhotoInfo(image.toUri(), location)
    }


    fun setPhotoInfo(image: String, location: String, photo: SavePhotoItem) {
        showProgressBar(true)
        loadImagePhotoInfo(image,location)
        repository.setPhotoInfo(photo)
        repository.entry = { it ->
            if (it) {
                entry(true)
                showProgressBar(false)
            } else {
                entry(false)
                showProgressBar(false)
            }
        }
    }

    suspend fun getMalls() {
        repository.getMallsFromFirebase()
        repository.malls.observeForever {
            listMallsName.postValue(repository.malls.value)
        }
    }


    fun setPhotoFromDBRealTime(photo: PhotoLikesInfo) {
        repository.setPhotoFromDBRealTime(photo)
    }

    fun nickName(): String = repository.nickName()


}
package com.example.diplom.ui.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.diplom.model.*
import com.google.firebase.ktx.Firebase
import com.example.diplom.model.AllPhoto
import com.example.diplom.model.FriendRequest
import com.example.diplom.model.GalleryPhotoItem
import com.example.diplom.model.SavePhotoItem
import com.example.diplom.repository.FirebaseRepository
import com.example.diplom.repository.PhotoUserRepository
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyProfileViewModel @Inject constructor(private val repository: FirebaseRepository) :
    ViewModel() {

    lateinit var requestStatus: (s: String) -> Unit
    val list = MutableLiveData<ArrayList<GalleryPhotoItem>>()
    private val storage = Firebase.storage
    val listPhotoItemRTDB = MutableLiveData<ArrayList<PhotoLikesInfo>>()
    val friendsList = MutableLiveData<ArrayList<FriendRequest>>()
    val requestsList = MutableLiveData<ArrayList<FriendRequest>>()

    fun nickName() = repository.getUserNickName()
    fun photo() = repository.getUserPhoto()

    val listOfAllPhoto = MutableLiveData<ArrayList<SavePhotoItem>>()

    private fun getPhoto() {
        PhotoUserRepository.getAllPhotoFromFirebase()
        PhotoUserRepository.allPhoto.observeForever {
            listOfAllPhoto.postValue(PhotoUserRepository.allPhoto.value)
        }
        listOfAllPhoto.observeForever {
            AllPhoto.allPhotoList.addAll(it)
        }
    }

    fun getAllUsers() {
        repository.getInfoAboutUsersFromFirebase()
        repository.listOfUsers.observeForever {
            AllUsers.allUserList.addAll(it)
        }
        repository.listOfUsers.observeForever {
            AllUsers.allUserList.addAll(it)
        }
    }

    fun loadPhoto() {
        getPhoto()
        PhotoUserRepository.loadPhoto()
        PhotoUserRepository.photoProfileList.observeForever {
            list.postValue(PhotoUserRepository.photoProfileList.value)
        }
    }

    fun getDataFromDB() {
        repository.getDataFromDB()
        repository.dataList.observeForever {
            listPhotoItemRTDB.postValue(repository.dataList.value)
        }
    }


    fun signOut() = repository.signOut()

    fun addFriend(email: String) {
        repository.makeToast = {
            requestStatus(it)
        }
        repository.sendFriendRequest(email)
    }

    suspend fun checkRequests(){
        repository.getRequests()
        repository.requestList.observeForever {
            requestsList.postValue(it)
        }
        repository.friendList.observeForever {
            friendsList.postValue(it)
        }
    }

    fun acceptFriendRequest(request: FriendRequest){
        repository.acceptRequest(request)
    }

}

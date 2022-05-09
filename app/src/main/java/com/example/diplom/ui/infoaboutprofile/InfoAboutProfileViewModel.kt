package com.example.diplom.ui.infoaboutprofile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.diplom.model.*
import com.example.diplom.model.AllUsers.allUserList
import com.example.diplom.repository.FirebaseRepository
import com.example.diplom.repository.PhotoUserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InfoAboutProfileViewModel @Inject constructor(private val repository: FirebaseRepository) :
    ViewModel() {

    private var auth = FirebaseAuth.getInstance()

    var nameOfUser: String? = null
    var surname: String? = null
    var nickname: String? = null
    var city: String? = null
    var emailOfUser: String? = null


    fun photo(): String = auth.currentUser?.photoUrl.toString()


    fun getUser() {
        allUserList.forEach {
            if (it.email!! == auth.currentUser!!.email) {
                nameOfUser = it.name
                surname = it.surname
                nickname = it.nickname
                city = it.city
                emailOfUser = it.email
            }
        }
    }

    fun singOut() = auth.signOut()


}
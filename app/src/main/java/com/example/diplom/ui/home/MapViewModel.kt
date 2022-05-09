package com.example.diplom.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.diplom.model.Mall
import com.example.diplom.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(private val repository: FirebaseRepository) : ViewModel() {

    val listmalls = MutableLiveData<ArrayList<Mall>>()

    suspend fun getMalls(){
        repository.getMallsFromFirebase()
        repository.malls.observeForever{
            listmalls.postValue(repository.malls.value)
        }
    }
}
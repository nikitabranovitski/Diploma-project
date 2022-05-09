package com.example.diplom.ui.messages

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.diplom.model.Chat
import com.example.diplom.model.Message
import com.example.diplom.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(private val repository: FirebaseRepository): ViewModel() {

    val chatsList = MutableLiveData<ArrayList<Chat>>(arrayListOf())

    fun getChats(){
        repository.getListOfMessages()
        repository.chatList.observeForever {
            chatsList.postValue(it)
        }

    }

    fun getCurrentEmail() = repository.email

    fun setNewMessage(list: ArrayList<Chat>, message: Chat){
        repository.setNewMessage(list, message)
    }

}
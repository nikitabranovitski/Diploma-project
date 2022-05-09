package com.example.diplom.ui.login

import androidx.lifecycle.ViewModel
import com.example.diplom.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: FirebaseRepository) : ViewModel() {

    lateinit var entry: (bool: Boolean) -> Unit

    private var disposable: Disposable? = null

    override fun onCleared() {
        super.onCleared()
        disposable?.dispose()
    }

    fun checkUser(){
        if (repository.checkUser()) {
            entry(true)
        }else{
            entry(false)
        }
    }

    fun login(email: String, pass: String) {
        disposable = repository.signIn(email, pass)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                entry(true)
            }, {
                entry(false)
            })
    }

    fun loginWithGoogle(idToken: String){
        disposable  = repository.firebaseAuthWithGoogle(idToken)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                entry(true)
            }, {
                entry(false)
            })
    }
}
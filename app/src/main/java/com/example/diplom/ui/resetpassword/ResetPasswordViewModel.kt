package com.example.diplom.ui.resetpassword

import androidx.lifecycle.ViewModel
import com.example.diplom.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(private var repository: FirebaseRepository) :
    ViewModel() {

    lateinit var entry: (bool: Boolean) -> Unit

    private var disposable: Disposable? = null

    override fun onCleared() {
        super.onCleared()
        disposable?.dispose()
    }

    fun resetPass(email: String) {
        disposable = repository.resetPassword(email)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                entry(true)
            }, {
                entry(false)
            })
    }
}
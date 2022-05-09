package com.example.diplom.ui.registration

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.diplom.model.User
import com.example.diplom.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(private val repository: FirebaseRepository) :
    ViewModel() {

    lateinit var entry: (bool: Boolean) -> Unit


    private var disposable: Disposable? = null

    override fun onCleared() {
        super.onCleared()
        disposable?.dispose()
    }

    private fun loadImage(image: Uri?) {
        repository.loadImage(image)
        repository.entry = { it ->
            if (it) {
                entry(true)
            } else {
                entry(false)
            }
        }
    }

    fun register(user: User, image: Uri?) {
        disposable = repository.register(user)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                entry(true)
                loadImage(image)
            }, {
                entry(false)
            })
    }
}
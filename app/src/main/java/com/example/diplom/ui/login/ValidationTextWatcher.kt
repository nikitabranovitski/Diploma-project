package com.example.diplom.ui.login

import android.text.Editable
import android.text.TextWatcher

class ValidationTextWatcher(private val check: (s: String) -> Unit) : TextWatcher {
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        //do nothing
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        //do nothing
    }

    override fun afterTextChanged(p0: Editable?) {
        check(p0.toString())
    }
}
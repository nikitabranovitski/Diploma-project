package com.example.diplom.ui.profile

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.diplom.databinding.DialogSignOutBinding

class AddSignOutDialog : DialogFragment() {

    lateinit var signOut:()->Unit

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = DialogSignOutBinding.inflate(layoutInflater).root

        return AlertDialog.Builder(requireContext())
            .setView(view)
            .setTitle("Are you sure?")
            .setPositiveButton("Yes,i want to sign out"){_,_->
                signOut()
                dismiss()
            }.setNegativeButton("Dismiss"){_,_->
                dismiss()
            }
            .create()
    }
}
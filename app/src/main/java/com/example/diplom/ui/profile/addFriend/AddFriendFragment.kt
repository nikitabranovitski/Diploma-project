package com.example.diplom.ui.profile.addFriend

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.example.diplom.R
import com.example.diplom.databinding.FragmentAddFriendBinding
import com.example.diplom.model.Message
import com.example.diplom.ui.login.ValidationTextWatcher
import com.example.diplom.ui.profile.MyProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddFriendFragment : Fragment() {

    private lateinit var binding: FragmentAddFriendBinding
    private val viewModel by viewModels<MyProfileViewModel>()

    private var friendEmail = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddFriendBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("ShowToast")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.emailEditText.addTextChangedListener(ValidationTextWatcher {
            if (Patterns.EMAIL_ADDRESS.matcher(it).matches()) {
                friendEmail = it
                binding.confirmButton.isEnabled = true
            } else {
                binding.confirmButton.isEnabled = false
            }
        })

        binding.confirmButton.setOnClickListener {
            viewModel.requestStatus = { s ->
                Toast.makeText(requireContext(), s, Toast.LENGTH_LONG).show()
            }
            viewModel.addFriend(friendEmail)
            view.findNavController().navigate(R.id.action_addFriendFragment_to_myProfileFragment)
//            viewModel.setInDBMessages(
//                Message(
//                    viewModel.getCurrentEmail().replace(
//                        "[^A-Za-zА-Яа-я0-9]".toRegex(),
//                        ""
//                    ),
//                    binding.emailEditText.text.toString()
//                        .replace("[^A-Za-zА-Яа-я0-9]".toRegex(), ""),
//                    arrayListOf()
//                )
//            )
        }
    }
}
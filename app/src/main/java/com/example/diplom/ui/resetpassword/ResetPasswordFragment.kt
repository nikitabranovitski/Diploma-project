package com.example.diplom.ui.resetpassword

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.diplom.R
import com.example.diplom.databinding.FragmentResetPasswordBinding
import com.example.diplom.ui.login.LoginFragment
import com.example.diplom.ui.registration.RegistrationFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResetPasswordFragment : Fragment() {

    private lateinit var binding: FragmentResetPasswordBinding
    private val viewModel by viewModels<ResetPasswordViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentResetPasswordBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.apply {
            entry = {
                if (it) {
                    Toast.makeText(requireContext(), "Success", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_LONG).show()
                }
            }
        }
        binding.sendButton.setOnClickListener {
            viewModel.resetPass(binding.sendEmailEditText.text.toString().trim())
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container, LoginFragment()).commit()
        }
    }

}
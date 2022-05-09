package com.example.diplom.ui.registration

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.diplom.R
import com.example.diplom.databinding.FragmentRegistrationBinding
import com.example.diplom.model.User
import com.example.diplom.ui.login.ValidationTextWatcher
import com.example.diplom.util.passwordPattern
import com.example.diplom.ui.home.HomeActivity
import com.example.diplom.ui.login.LoginFragment
import com.example.diplom.util.FLAG
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegistrationFragment : Fragment() {

    private lateinit var binding: FragmentRegistrationBinding

    private var imageUri: Uri? = null

    private val viewModel by viewModels<RegistrationViewModel>()

    private var login: String = ""
    private var isLoginEntered = false
    private var pass: String = ""
    private var isPassEntered = false
    private var isPasswordMatch = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistrationBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.apply {
            entry = {
                if (it) {
                    Toast.makeText(requireContext(), "Success", Toast.LENGTH_LONG).show()
                    FLAG = false
                    requireActivity().run {
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    }
                } else {
                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.avatarImageView.setOnClickListener {
            pickImage()
        }

        binding.loginEditText.addTextChangedListener(ValidationTextWatcher {
            it.trim()
            if (Patterns.EMAIL_ADDRESS.matcher(it).matches()) {
                isLoginEntered = true
                login = it
                binding.loginWarning.visibility = View.GONE
            } else {
                isLoginEntered = false
                binding.loginWarning.visibility = View.VISIBLE
            }
            binding.registerButton.isEnabled = isLoginEntered && isPassEntered && isPasswordMatch
        })

        binding.passwordEditText.addTextChangedListener(ValidationTextWatcher {
            it.trim()
            if (passwordPattern.matcher(it).matches()) {
                isPassEntered = true
                pass = it
                binding.passwordWarning.visibility = View.GONE
            } else {
                isPassEntered = false
                binding.passwordWarning.visibility = View.VISIBLE
            }
            binding.registerButton.isEnabled = isLoginEntered && isPassEntered && isPasswordMatch
        })

        binding.confirmPasswordEditText.addTextChangedListener(ValidationTextWatcher {
            it.trim()
            if (passwordPattern.matcher(it).matches() && it == pass) {
                isPasswordMatch = true
                binding.passwordConfirmationWarning.visibility = View.GONE
            } else {
                isPasswordMatch = false
                binding.passwordConfirmationWarning.visibility = View.VISIBLE
            }
            binding.registerButton.isEnabled = isLoginEntered && isPassEntered && isPasswordMatch

        })

        binding.registerButton.setOnClickListener {
            val user = User(
                binding.nameEditText.text.toString().trim(),
                binding.surnameEditText.text.toString().trim(),
                binding.nicknameEditText.text.toString().trim(),
                binding.cityEditText.text.toString().trim(),
                login, pass
            )
            viewModel.register(user, imageUri)
        }
    }

    private fun pickImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            imageUri = data!!.data!!
            binding.avatarImageView.setImageURI(imageUri)
        }
    }

}
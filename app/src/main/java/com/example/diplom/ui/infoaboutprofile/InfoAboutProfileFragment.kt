package com.example.diplom.ui.infoaboutprofile

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.diplom.databinding.FragmentInfoAboutProfileBinding
import com.example.diplom.repository.PhotoUserRepository
import com.example.diplom.ui.MainActivity
import com.example.diplom.util.loadUrl
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class InfoAboutProfileFragment : Fragment() {

    private lateinit var binding: FragmentInfoAboutProfileBinding

    private val viewModel by viewModels<InfoAboutProfileViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInfoAboutProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getUser()
        binding.run {
            viewModel.run {
                avatarImageView.loadUrl(photo())
                cityEditText.text = "City: $city"
                nameEditText.text = "Name: $nameOfUser"
                surnameEditText.text = "Surname: $surname"
                nicknameEditText.text = "Nickname: $nickname"
                loginEditText.text = "Email: $emailOfUser"
            }
        }
        binding.logoutButton.setOnClickListener {
            viewModel.singOut()
            activity?.startActivity(Intent(activity, MainActivity::class.java))
        }
    }

}
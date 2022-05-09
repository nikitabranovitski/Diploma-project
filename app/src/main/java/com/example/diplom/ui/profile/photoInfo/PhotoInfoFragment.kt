package com.example.diplom.ui.profile.photoInfo

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.diplom.databinding.FragmentPhotoInfoBinding
import com.example.diplom.model.AllPhoto
import com.example.diplom.model.SavePhotoItem
import com.example.diplom.repository.PhotoUserRepository
import com.example.diplom.util.loadPhotoGlideApp
import com.example.diplom.util.photoItem
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PhotoInfoFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentPhotoInfoBinding
    private lateinit var navController: NavController
    private val viewModel by viewModels<PhotoInfoViewModel>()
    private lateinit var photoUserItem: SavePhotoItem
    private lateinit var path: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhotoInfoBinding.inflate(layoutInflater)
        return binding.root
    }


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.apply {
            entry = {
                if (it) {
                    Toast.makeText(
                        requireContext(),
                        "Success",
                        Toast.LENGTH_LONG
                    ).show()
                    dismiss()
                } else {
                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_LONG).show()
                }
            }

            showProgressBar = {
                binding.progress.post {
                    binding.progress.visibility = if (it) {
                        View.VISIBLE
                    } else View.GONE
                }
            }
        }

        navController = NavHostFragment.findNavController(this)

        photoItem!!.URLPhoto.let { binding.photoImageView.loadPhotoGlideApp(it) }
        AllPhoto.allPhotoList.forEach { item ->
            if (photoItem!!.URLPhoto.contains(item.id!!)) {
                path = PhotoUserRepository.email + "|" + item.id
                photoUserItem = item
                binding.priceTextView.text = "price - ${item.price} BYN"
                binding.locationTextView.text = "mall - ${item.nameMall}"
            }
        }

        binding.deletePhotoImageView.setOnClickListener {
            viewModel.deletePhoto(photoUserItem,path)
        }

    }
}
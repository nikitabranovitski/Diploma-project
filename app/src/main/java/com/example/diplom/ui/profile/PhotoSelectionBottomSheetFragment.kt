package com.example.diplom.ui.profile

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.diplom.databinding.DialogBottomPhotoSelectionBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PhotoSelectionBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: DialogBottomPhotoSelectionBinding
    private lateinit var navController: NavController
    private var image: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogBottomPhotoSelectionBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = NavHostFragment.findNavController(this)

        binding.galleryChoiceButton.setOnClickListener {
            pickImage()
        }
        binding.cameraChoiceButton.setOnClickListener {
            openCamera()
        }
    }

    private fun pickImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 200)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 200) {
            with(navController) {
                navigate(
                    PhotoSelectionBottomSheetFragmentDirections.actionPhotoSelectionBottomSheetFragmentToFragmentSavePhoto(
                        data!!.data!!.toString()
                    )
                )
            }
        }
        if (requestCode == IMAGE_CAPTURE_CODE && resultCode == Activity.RESULT_OK) {
            if (image != null) {
                with(navController) {
                    navigate(
                        PhotoSelectionBottomSheetFragmentDirections.actionPhotoSelectionBottomSheetFragmentToFragmentSavePhoto(
                            image.toString()
                        )
                    )
                }
            }
        }
    }

    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        image = requireContext().contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        )
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image)
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
    }

    companion object {
        private const val RESULT_LOAD_IMAGE = 123
        private const val IMAGE_CAPTURE_CODE = 654
    }
}
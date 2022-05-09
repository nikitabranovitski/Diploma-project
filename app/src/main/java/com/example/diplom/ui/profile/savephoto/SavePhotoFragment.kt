package com.example.diplom.ui.profile.savephoto

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.diplom.databinding.FragmentSavePhotoBinding
import com.example.diplom.model.SavePhotoItem
import com.example.diplom.repository.PhotoUserRepository
import dagger.hilt.android.AndroidEntryPoint
import android.widget.ArrayAdapter
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import androidx.lifecycle.lifecycleScope
import com.example.diplom.R
import com.example.diplom.model.Mall
import com.example.diplom.ui.login.ValidationTextWatcher
import com.example.diplom.util.pricePattern
import kotlinx.coroutines.launch
import com.example.diplom.model.PhotoLikesInfo


@AndroidEntryPoint
class SavePhotoFragment : Fragment() {

    private lateinit var binding: FragmentSavePhotoBinding
    private val viewModel by viewModels<SavePhotoViewModel>()
    private lateinit var navController: NavController
    private var listMall = mutableListOf<Mall>()
    private var price = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSavePhotoBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = view.findNavController()

        viewModel.apply {
            entry = {
                if (it) {
                    Toast.makeText(
                        requireContext(),
                        "Success",
                        Toast.LENGTH_LONG
                    ).show()
                    PhotoUserRepository.loadPhoto()
                    navController.navigate(SavePhotoFragmentDirections.actionFragmentSavePhotoToMyProfileFragment())
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
        lifecycleScope.launch{
        viewModel.getMalls()
        }
        viewModel.listMallsName.observe(viewLifecycleOwner) {
            setDataList(it)
            val adapterSpinner = ArrayAdapter(
                requireContext(),
                R.layout.spinner_layout,
                listMall.map {
                    it.name
                }
            )
            adapterSpinner.setDropDownViewResource(R.layout.spinner_layout)
            binding.nameMallEditText.adapter = adapterSpinner

        }


        val args: SavePhotoFragmentArgs by navArgs()
        binding.photoImageView.setImageURI(args.uri.toUri())

        binding.priceEditText.addTextChangedListener(ValidationTextWatcher {
            if (pricePattern.matcher(it).matches()&& it[0] != '.') {
                price = it
                binding.priceWarning.visibility = View.GONE
                binding.saveImageInformation.isEnabled = true
            }else{
                binding.priceWarning.visibility = View.VISIBLE
                binding.saveImageInformation.isEnabled = false
            }
        })


        binding.saveImageInformation.setOnClickListener {
            val myPhotoInfo = SavePhotoItem(
                id = args.uri.hashCode().toString() + binding.priceEditText.text.toString()
                    .hashCode(),
                price = binding.priceEditText.text.toString().trim(),
                nameMall = binding.nameMallEditText.selectedItem.toString().trim()
            )
            val itemPhotoLike = PhotoLikesInfo(
                myPhotoInfo.id.toString()
            )

            viewModel.setPhotoFromDBRealTime(itemPhotoLike)
            viewModel.setPhotoInfo(args.uri, myPhotoInfo.id.toString(), myPhotoInfo)
        }
    }

    private fun setDataList(data: ArrayList<Mall>) {
        listMall = data
    }
}
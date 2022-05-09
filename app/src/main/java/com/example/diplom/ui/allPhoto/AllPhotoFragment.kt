package com.example.diplom.ui.allPhoto

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.diplom.databinding.FragmentAllPhotoBinding
import com.example.diplom.model.AllPhoto
import com.example.diplom.model.GalleryPhotoItem
import com.example.diplom.model.PhotoLike
import com.example.diplom.repository.PhotoUserRepository
import com.example.diplom.ui.allPhoto.adapter.AllPhotoAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllPhotoFragment : Fragment() {

    private lateinit var binding: FragmentAllPhotoBinding

    private val viewModel by viewModels<AllPhotoViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllPhotoBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getDataFromDB()
        viewModel.loadPhoto()
        viewModel.list.observe(viewLifecycleOwner) {
            setList(it)
        }
    }

    private fun setList(list: ArrayList<GalleryPhotoItem>) {
        binding.containerAllPhoto.run {
            if (adapter == null) {
                adapter = AllPhotoAdapter(requireContext()){
                    it.contains(viewModel.getCurrentUserEmail())
                }
                layoutManager = LinearLayoutManager(requireContext())
            }
            PhotoUserRepository.allPhotoList.value?.let {
                (binding.containerAllPhoto.adapter as? AllPhotoAdapter)?.setDataList(
                    it
                )
            }
            (binding.containerAllPhoto.adapter as? AllPhotoAdapter)?.clickListener = {it, it1 ->
                viewModel.setLike(it.URLPhoto.substringAfterLast("|"), it1)
            }
            (binding.containerAllPhoto.adapter as? AllPhotoAdapter)?.delClickListener = {it, it1 ->
                viewModel.delLike(it.URLPhoto.substringAfterLast("|"), it1)
            }
        }
    }

}
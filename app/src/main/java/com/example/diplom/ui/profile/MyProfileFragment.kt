package com.example.diplom.ui.profile

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.navigation.findNavController
import android.widget.PopupMenu
import androidx.annotation.MenuRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.recyclerview.widget.GridLayoutManager
import com.example.diplom.R
import com.example.diplom.databinding.FragmentMyProfileBinding
import com.example.diplom.ui.profile.adapter.MyProfileAdapter
import com.example.diplom.util.loadUrl
import dagger.hilt.android.AndroidEntryPoint
import com.example.diplom.repository.PhotoUserRepository
import com.example.diplom.util.loadPhotoGlideApp
import com.example.diplom.util.photoItem

import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyProfileFragment : Fragment() {
    private lateinit var binding: FragmentMyProfileBinding

    private lateinit var image: String
    private lateinit var navController: NavController
    private lateinit var navGraph: NavGraph

    private val viewModel by viewModels<MyProfileViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        viewModel.loadPhoto()
        viewModel.list.observe(viewLifecycleOwner) {
            PhotoUserRepository.photoProfileList.value?.let { it1 ->
                (binding.containerPhoto.adapter as MyProfileAdapter).setDataList(it1)
            }
            binding.countPhotoTextView.text =
                "${viewModel.list.value?.size}\n${getString(R.string.posts)}"
        }
        lifecycleScope.launch {
            viewModel.checkRequests()
        }
        viewModel.friendsList.observe(viewLifecycleOwner) {
            binding.countFriendsTextView.text =
                viewModel.friendsList.value?.size.toString() + "\nFriends"
        }
        viewModel.requestsList.observe(viewLifecycleOwner) {
            viewModel.requestsList.value?.size!!.let {
                if (it == 0) {
                    binding.countRequestTextView.visibility = View.GONE
                } else if (it > 99) {
                    binding.countRequestTextView.visibility = View.VISIBLE
                    binding.countRequestTextView.text = "99+"
                } else {
                    binding.countRequestTextView.visibility = View.VISIBLE
                    binding.countRequestTextView.text = it.toString()
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        image = viewModel.photo()
        viewModel.getAllUsers()
        navController = view.findNavController()

        binding.emailUserTextView.text = viewModel.nickName()

        if (image == "null") {
            image = PhotoUserRepository.photoUserAvatar.toString()
            binding.profileImage.loadPhotoGlideApp(image)
        } else {
            binding.profileImage.loadUrl(image)
        }
        binding.profileImage.loadUrl(viewModel.photo())

        navController = view.findNavController()
        navGraph = navController.navInflater.inflate(R.navigation.navigation_my_profile)

        binding.containerPhoto.run {
            adapter = MyProfileAdapter(requireContext()) {
                photoItem = it
                navController.navigate(MyProfileFragmentDirections.actionMyProfileFragmentToPhotoInfoFragment())
            }
            layoutManager = GridLayoutManager(requireContext(), 3)
        }
        binding.containerPhoto.isNestedScrollingEnabled = true

        binding.addPhotoImageButton.setOnClickListener {
            navController.navigate(R.id.action_myProfileFragment_to_photoSelectionBottomSheetFragment)
        }

        binding.menuButton.setOnClickListener {
            showMenu(it, R.menu.profile_side_menu)
        }

        binding.countRequestTextView.setOnClickListener {
            navController.navigate(R.id.action_myProfileFragment_to_requestsFragment)
        }

    }

    private fun showMenu(v: View, @MenuRes menuRes: Int) {
        val popup = PopupMenu(requireContext(), v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.addFriend -> {
                    navController.navigate(R.id.action_myProfileFragment_to_addFriendFragment)
                    true
                }
                R.id.edit -> {
                    navController.navigate(MyProfileFragmentDirections.actionMyProfileFragmentToInfoAboutProfileFragment())
                    true
                }
                R.id.signOut -> {
                    AddSignOutDialog().apply {
                        signOut = {
                            viewModel.signOut()
                            navController.navigate(R.id.action_myProfileFragment_to_mainActivity)
                        }
                    }.show(parentFragmentManager, "")
                    true
                }
                else -> {
                    false
                }
            }
        }
        popup.setOnDismissListener {

            binding.addPhotoImageButton.setOnClickListener {
                navController.graph =
                    navController.navInflater.inflate(R.navigation.navigation_my_profile)
                navController.navigate(R.id.action_myProfileFragment_to_photoSelectionBottomSheetFragment)
            }
        }
        popup.show()
    }
}
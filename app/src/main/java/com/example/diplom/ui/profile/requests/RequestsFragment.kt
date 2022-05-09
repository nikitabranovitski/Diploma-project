package com.example.diplom.ui.profile.requests

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.diplom.databinding.FragmentRequestsBinding
import com.example.diplom.ui.profile.MyProfileViewModel
import com.example.diplom.ui.profile.requests.adapter.FriendRequestAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RequestsFragment : Fragment() {

    private lateinit var binding: FragmentRequestsBinding
    private val viewModel by viewModels<MyProfileViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRequestsBinding.inflate(inflater)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            viewModel.checkRequests()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.requestContainer.run {
            if (adapter == null)
                adapter = FriendRequestAdapter(requireContext()) {
                    viewModel.acceptFriendRequest(it)
                    (adapter as FriendRequestAdapter).listFriends.remove(it)
                }
            layoutManager = LinearLayoutManager(requireContext())
            isNestedScrollingEnabled = true
            viewModel.requestsList.observe(viewLifecycleOwner) {
                (adapter as FriendRequestAdapter).setList(it)
            }
        }
    }
}
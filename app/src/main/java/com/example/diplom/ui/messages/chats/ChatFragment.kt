package com.example.diplom.ui.messages.chats

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.diplom.R
import com.example.diplom.databinding.FragmentChatBinding
import com.example.diplom.model.Chat
import com.example.diplom.model.Message
import com.example.diplom.ui.messages.MessageViewModel
import com.example.diplom.ui.messages.adapter.ChatAdapter
import com.example.diplom.ui.messages.adapter.MessageAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding

    private val viewModel by viewModels<MessageViewModel>()

    private var listOfMessages: ArrayList<Chat> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onPause() {
        super.onPause()
        viewModel.chatsList.observe(viewLifecycleOwner) {
            listOfMessages.addAll(it)
            setList(it)
            view?.requestLayout()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getChats()
        viewModel.chatsList.observe(viewLifecycleOwner) {
            listOfMessages.addAll(it)
            setList(it)
        }

        binding.sendButton.setOnClickListener {
            viewModel.setNewMessage(
                listOfMessages,
                Chat(viewModel.getCurrentEmail() + ": " + binding.messageEditText.text.toString())
            )
            binding.messageEditText.setText("")
                    listOfMessages = arrayListOf()
        }

    }

    private fun setList(list: ArrayList<Chat>) {
            binding.containerMessage.run {
                if (adapter == null) {
                    adapter = ChatAdapter(requireContext())
                    layoutManager = LinearLayoutManager(requireContext())
                }
                (binding.containerMessage.adapter as? ChatAdapter)?.setDataList(list)
            }


    }

}
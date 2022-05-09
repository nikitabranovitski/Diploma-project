package com.example.diplom.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.diplom.databinding.FragmentMallInfoBinding
import com.example.diplom.model.Mall
import com.example.diplom.util.loadPhotoGlideApp
import com.example.diplom.util.loadUrl

class MallInfoFragment : Fragment() {

    private lateinit var binding: FragmentMallInfoBinding
    private lateinit var mall: Mall

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMallInfoBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: MallInfoFragmentArgs by navArgs()

        binding.apply {
            mallName.text = args.mall.name
            mallImage.loadPhotoGlideApp("malls/${args.mall.id}.jpg")
            address.text = args.mall.adress
            email.text = args.mall.email
            phone.text = args.mall.contactPhone
            website.text = args.mall.webSite
            vk.text = args.mall.vk
            instagram.text = args.mall.instagram
            facebook.text = args.mall.facebook
        }
    }

    companion object {
        private var mallInfoFragment: MallInfoFragment? = null
        fun newInstance(_mall: Mall): MallInfoFragment {
            if (mallInfoFragment == null)
                mallInfoFragment = MallInfoFragment()
            mallInfoFragment?.apply {
                mall = _mall
            }
            return mallInfoFragment as MallInfoFragment
        }
    }
}
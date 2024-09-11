package com.example.voicechanger.fragment

import android.os.Bundle
import com.example.voicechanger.R
import com.example.voicechanger.base.fragment.BaseFragmentNotRequireViewModel
import com.example.voicechanger.databinding.FragmentIntroduceBinding
import com.example.voicechanger.util.loadImage

class IntroduceFragment : BaseFragmentNotRequireViewModel<FragmentIntroduceBinding>(R.layout.fragment_introduce) {
    companion object {
        private const val ARG_IMAGE = "image"
        private const val ARG_TEXT_TITLE = "textTitle"
        private const val ARG_TEXT_CONTENT = "textContent"

        fun newInstance(image: Int, textTitle: Int, textContent: Int): IntroduceFragment {
            val fragment = IntroduceFragment()
            val args = Bundle()
            args.putInt(ARG_IMAGE, image)
            args.putInt(ARG_TEXT_TITLE, textTitle)
            args.putInt(ARG_TEXT_CONTENT, textContent)
            fragment.arguments = args
            return fragment
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        val image = arguments?.getInt(ARG_IMAGE)
        val textTitle = arguments?.getInt(ARG_TEXT_TITLE)
        val textContent = arguments?.getInt(ARG_TEXT_CONTENT)

        binding.imgBackground.loadImage(image ?: 0, false)
        binding.tvTitle.text = getString(textTitle ?: 0)
        binding.tvContent.text = getString(textContent ?: 0)
    }
}
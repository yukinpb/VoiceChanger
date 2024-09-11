package com.example.voicechanger.util

import com.example.voicechanger.R
import com.example.voicechanger.fragment.IntroduceFragment

object IntroduceFragmentProvider {
    fun getFragments(): List<IntroduceFragment> {
        return listOf(
            IntroduceFragment.newInstance(R.mipmap.bg_tutorial_1, R.string.title_tutorial_1, R.string.content_tutorial_1),
            IntroduceFragment.newInstance(R.mipmap.bg_tutorial_2, R.string.title_tutorial_2, R.string.content_tutorial_2),
            IntroduceFragment.newInstance(R.mipmap.bg_tutorial_3, R.string.title_tutorial_3, R.string.content_tutorial_3),
            IntroduceFragment.newInstance(R.mipmap.bg_tutorial_4, R.string.title_tutorial_4, R.string.content_tutorial_4)
        )
    }
}
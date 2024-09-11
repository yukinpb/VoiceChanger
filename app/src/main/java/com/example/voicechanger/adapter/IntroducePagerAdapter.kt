package com.example.voicechanger.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.voicechanger.fragment.IntroduceFragment

class IntroducePagerAdapter(
    fragmentActivity: FragmentActivity,
    private val fragments: List<IntroduceFragment>
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]
}
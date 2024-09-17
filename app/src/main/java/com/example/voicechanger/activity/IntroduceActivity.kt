package com.example.voicechanger.activity

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.voicechanger.R
import com.example.voicechanger.adapter.IntroducePagerAdapter
import com.example.voicechanger.base.activity.BaseActivityNotRequireViewModel
import com.example.voicechanger.pref.AppPreferences
import com.example.voicechanger.databinding.ActivityIntroduceBinding
import com.example.voicechanger.util.IntroduceFragmentProvider
import com.example.voicechanger.util.setOnSafeClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class IntroduceActivity : BaseActivityNotRequireViewModel<ActivityIntroduceBinding>() {
    private val fragments = IntroduceFragmentProvider.getFragments()

    @Inject
    lateinit var appPreferences: AppPreferences

    override val layoutId: Int
        get() = R.layout.activity_introduce

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        val adapter = IntroducePagerAdapter(this, fragments)
        binding.vpIntroduce.adapter = adapter
        binding.dotIndicator.attachTo(binding.vpIntroduce)
    }

    override fun setOnClick() {
        super.setOnClick()

        binding.vpIntroduce.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == fragments.size - 1) {
                    binding.btnNext.text = getString(R.string.start)
                } else {
                    binding.btnNext.text = getString(R.string.next)
                }
            }
        })

        binding.btnNext.setOnSafeClickListener {
            val currentItem = binding.vpIntroduce.currentItem
            if (currentItem < fragments.size - 1) {
                binding.vpIntroduce.currentItem = currentItem + 1
            } else {
                lifecycleScope.launch {
                    appPreferences.setShouldShowIntroduce(false)
                }
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }
}
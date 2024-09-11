package com.example.voicechanger.activity

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.example.voicechanger.R
import com.example.voicechanger.base.activity.BaseActivityNotRequireViewModel
import com.example.voicechanger.base.pref.AppPreferences
import com.example.voicechanger.databinding.ActivitySplashBinding
import com.example.voicechanger.util.loadImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : BaseActivityNotRequireViewModel<ActivitySplashBinding>() {

    @Inject
    lateinit var appPreferences: AppPreferences

    override val layoutId: Int
        get() = R.layout.activity_splash

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        binding.splashImage.loadImage(R.mipmap.img_slpash, true)
    }

    override fun bindingAction() {
        super.bindingAction()

        lifecycleScope.launch {
            showLoading()

            delay(2000)

            hiddenLoading()

            val shouldShowIntroduce = appPreferences.shouldShowIntroduce().first() ?: true
            if (shouldShowIntroduce) {
                startActivity(Intent(this@SplashActivity, IntroduceActivity::class.java))
            } else {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            }
            finish()
        }
    }
}
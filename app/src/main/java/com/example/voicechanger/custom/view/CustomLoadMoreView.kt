package com.example.voicechanger.custom.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import com.example.voicechanger.R
import com.example.voicechanger.databinding.LayoutLoadMoreBinding

class CustomLoadMoreView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding: LayoutLoadMoreBinding = LayoutLoadMoreBinding.inflate(
        LayoutInflater.from(context), this, true
    )

    fun startLoading() {
        val rotateAnimation = AnimationUtils.loadAnimation(context, R.anim.rotate_infinite)
        binding.ivLoading.startAnimation(rotateAnimation)
    }

    fun stopLoading() {
        binding.ivLoading.clearAnimation()
    }
}
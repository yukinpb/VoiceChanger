package com.example.voicechanger.custom.toolbar

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.example.voicechanger.databinding.CustomToolbarBinding

class CustomToolbar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: CustomToolbarBinding

    init {
        val inflater = LayoutInflater.from(context)
        binding = CustomToolbarBinding.inflate(inflater, this, true)
    }

    fun setToolbarTitle(title: String) {
        binding.tvTitle.text = title
    }

    fun setUpBackButton(isVisible: Boolean = true, listener: () -> Unit = {}) {
        binding.btnBack.isVisible = isVisible
        if (isVisible) {
            binding.btnBack.setOnClickListener {
                listener.invoke()
            }
        }
    }

    fun setUpSettingButton(isVisible: Boolean = true, listener: () -> Unit = {}) {
        binding.btnSettings.isVisible = isVisible
        if (isVisible) {
            binding.btnSettings.setOnClickListener {
                listener.invoke()
            }
        }
    }

    fun setUpOkButton(isVisible: Boolean = true, listener: () -> Unit = {}) {
        binding.btnOk.isVisible = isVisible
        if (isVisible) {
            binding.btnOk.setOnClickListener {
                listener.invoke()
            }
        }
    }

    fun setUpMenuButton(isVisible: Boolean = true, listener: () -> Unit = {}) {
        binding.btnMenu.isVisible = isVisible
        if (isVisible) {
            binding.btnMenu.setOnClickListener {
                listener.invoke()
            }
        }
    }

    fun setUpHomeButton(isVisible: Boolean = true, listener: () -> Unit = {}) {
        binding.btnHome.isVisible = isVisible
        if (isVisible) {
            binding.btnHome.setOnClickListener {
                listener.invoke()
            }
        }
    }
}
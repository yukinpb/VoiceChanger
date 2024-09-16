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

    fun setToolbarTitle(title: String) : CustomToolbar {
        binding.tvTitle.text = title
        return this
    }

    fun setUpBackButton(isVisible: Boolean = true, listener: () -> Unit = {}) : CustomToolbar {
        binding.btnBack.isVisible = isVisible
        if (isVisible) {
            binding.btnBack.setOnClickListener {
                listener.invoke()
            }
        }
        return this
    }

    fun setUpSettingButton(isVisible: Boolean = true, listener: () -> Unit = {}) : CustomToolbar {
        binding.btnSettings.isVisible = isVisible
        if (isVisible) {
            binding.btnSettings.setOnClickListener {
                listener.invoke()
            }
        }
        return this
    }

    fun setUpOkButton(isVisible: Boolean = true, listener: () -> Unit = {}) : CustomToolbar {
        binding.btnOk.isVisible = isVisible
        if (isVisible) {
            binding.btnOk.setOnClickListener {
                listener.invoke()
            }
        }
        return this
    }

    fun setUpMenuButton(isVisible: Boolean = true, listener: () -> Unit = {}) : CustomToolbar {
        binding.btnMenu.isVisible = isVisible
        if (isVisible) {
            binding.btnMenu.setOnClickListener {
                listener.invoke()
            }
        }
        return this
    }

    fun setUpHomeButton(isVisible: Boolean = true, listener: () -> Unit = {}) : CustomToolbar {
        binding.btnHome.isVisible = isVisible
        if (isVisible) {
            binding.btnHome.setOnClickListener {
                listener.invoke()
            }
        }
        return this
    }
}
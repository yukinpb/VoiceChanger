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

    fun setBackButtonClickListener(listener: () -> Unit) {
        binding.btnBack.setOnClickListener {
            listener.invoke()
        }
    }

    fun setSettingButtonClickListener(listener: () -> Unit) {
        binding.btnSettings.setOnClickListener {
            listener.invoke()
        }
    }

    fun setOkButtonClickListener(listener: () -> Unit) {
        binding.btnOk.setOnClickListener {
            listener.invoke()
        }
    }

    fun setBackButtonVisibility(isVisible: Boolean) {
        binding.btnBack.isVisible = isVisible
    }

    fun setSettingButtonVisibility(isVisible: Boolean) {
        binding.btnSettings.isVisible = isVisible
    }

    fun setOkButtonVisibility(isVisible: Boolean) {
        binding.btnOk.isVisible = isVisible
    }
}
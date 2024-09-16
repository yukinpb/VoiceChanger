package com.example.voicechanger.custom.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import com.example.voicechanger.databinding.PopupAudioFileSortBinding

class AudioFileSortPopup(
    context: Context,
    private val onSortNewest: () -> Unit,
    private val onSortFileName: () -> Unit
) : PopupWindow(context) {

    private val binding: PopupAudioFileSortBinding =
        PopupAudioFileSortBinding.inflate(LayoutInflater.from(context))

    init {
        contentView = binding.root
        width = ViewGroup.LayoutParams.WRAP_CONTENT
        height = ViewGroup.LayoutParams.WRAP_CONTENT
        isOutsideTouchable = true
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        setupMenu()
    }

    private fun setupMenu() {
        setUpSortButton(true)

        binding.apply {
            btnSortByNewest.setOnClickListener {
                ivSortByNewest.isSelected = !ivSortByNewest.isSelected
                setUpSortButton(true)
                onSortNewest()
            }

            btnSortByName.setOnClickListener {
                ivSortByName.isSelected = !ivSortByName.isSelected
                setUpSortButton(false)
                onSortFileName()
            }
        }
    }

    private fun setUpSortButton(isNewestSelected: Boolean) {
        binding.apply {
            btnSortByNewest.isActivated = isNewestSelected
            btnSortByName.isActivated = !isNewestSelected
        }
    }

    fun show(anchorView: View) {
        val location = IntArray(2)
        anchorView.getLocationOnScreen(location)
        val xOffset = anchorView.width
        val yOffset = anchorView.height
        showAtLocation(anchorView, 0, location[0] + xOffset, location[1] + yOffset)
    }
}
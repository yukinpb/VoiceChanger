package com.example.voicechanger.custom.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import com.example.voicechanger.databinding.PopupAudioFileOptionsBinding

class AudioFileOptionsPopup(
    context: Context,
    private val onRename: () -> Unit,
    private val onSetAsRingtone: () -> Unit,
    private val onShare: () -> Unit,
    private val onDelete: () -> Unit
) : PopupWindow(context) {

    private val binding: PopupAudioFileOptionsBinding =
        PopupAudioFileOptionsBinding.inflate(LayoutInflater.from(context))

    init {
        contentView = binding.root
        width = ViewGroup.LayoutParams.WRAP_CONTENT
        height = ViewGroup.LayoutParams.WRAP_CONTENT
        isOutsideTouchable = true
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        setupMenu()
    }

    private fun setupMenu() {
        binding.apply {
            tvRename.setOnClickListener {
                onRename()
                dismiss()
            }
            tvSetAsRingtone.setOnClickListener {
                onSetAsRingtone()
                dismiss()
            }
            tvShare.setOnClickListener {
                onShare()
                dismiss()
            }
            tvDelete.setOnClickListener {
                onDelete()
                dismiss()
            }
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
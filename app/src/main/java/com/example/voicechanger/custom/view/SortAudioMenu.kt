package com.example.voicechanger.custom.view

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.example.voicechanger.databinding.LayoutMenuSortBinding

class SortAudioMenu(
    private val onSortNewest: () -> Unit,
    private val onSortFileName: () -> Unit,
) : DialogFragment() {

    private var binding: LayoutMenuSortBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        binding = LayoutMenuSortBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMenu()
    }

    private fun setupMenu() {
        binding?.apply {
            btnSortByNewest.setOnClickListener {
                btnSortByNewest.isActivated = !btnSortByNewest.isActivated
                toggleSortButtons(true)
                onSortNewest()
            }

            btnSortByName.setOnClickListener {
                btnSortByName.isActivated = !btnSortByName.isActivated
                toggleSortButtons(false)
                onSortFileName()
            }
        }
    }

    private fun toggleSortButtons(isNewestSelected: Boolean) {
        binding?.apply {
            tvSortByNewest.isActivated = isNewestSelected
            ivSortByNewest.isActivated = isNewestSelected

            tvSortByName.isActivated = !isNewestSelected
            ivSortByName.isActivated = !isNewestSelected
        }
    }

    override fun onResume() {
        super.onResume()
        val layoutParams = dialog?.window?.attributes
        layoutParams?.width = ViewGroup.LayoutParams.WRAP_CONTENT
        layoutParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog?.window?.attributes = layoutParams
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
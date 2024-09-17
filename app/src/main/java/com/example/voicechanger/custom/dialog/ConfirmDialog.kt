package com.example.voicechanger.custom.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.example.voicechanger.databinding.DialogConfirmBinding

class ConfirmDialog(
    private val title: String,
    private val content: String,
    private val onClickOK: () -> Unit
) : DialogFragment() {

    private var binding: DialogConfirmBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        binding = DialogConfirmBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.tvTitle?.text = title
        binding?.tvContent?.text = content

        binding?.buttonCancel?.setOnClickListener {
            dismiss()
        }

        binding?.buttonOk?.setOnClickListener {
            onClickOK()
            dismiss()
        }
    }

    override fun onResume() {
        super.onResume()

        val layoutParams = dialog?.window?.attributes
        layoutParams?.width = (resources.displayMetrics.widthPixels * 0.9).toInt()
        layoutParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog?.window?.attributes = layoutParams
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
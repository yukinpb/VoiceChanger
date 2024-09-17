package com.example.voicechanger.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.voicechanger.R
import com.example.voicechanger.adapter.LanguageAdapter
import com.example.voicechanger.databinding.DialogLanguageBinding
import com.example.voicechanger.model.Language
import java.util.Locale

class LanguageDialogFragment(
    private val onLanguageSelected: (Language) -> Unit
) : DialogFragment() {

    private var binding: DialogLanguageBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        binding = DialogLanguageBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val languages = listOf(
            Language(R.mipmap.ic_vietnam, getString(R.string.vietnamese), Locale("vi", "VN")),
            Language(R.mipmap.ic_us, getString(R.string.english), Locale.US)
        )

        binding?.rvLanguages?.layoutManager = LinearLayoutManager(context)
        binding?.rvLanguages?.adapter = LanguageAdapter(languages) { language ->
            onLanguageSelected(language)
            dismiss()
        }
    }

    override fun onResume() {
        super.onResume()

        val layoutParams = dialog?.window?.attributes
        layoutParams?.width = ViewGroup.LayoutParams.MATCH_PARENT
        layoutParams?.horizontalMargin = 0.1f
        dialog?.window?.attributes = layoutParams
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
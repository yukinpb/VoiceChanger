package com.example.voicechanger.custom.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.voicechanger.adapter.LanguageAdapter
import com.example.voicechanger.databinding.DialogLanguageBinding
import com.example.voicechanger.model.Language
import com.example.voicechanger.pref.AppPreferences
import com.example.voicechanger.util.LanguageProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LanguageDialog(
    private val onLanguageSelected: (Language) -> Unit
) : DialogFragment() {

    private var binding: DialogLanguageBinding? = null

    @Inject
    lateinit var appPreferences: AppPreferences

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

        val languages = LanguageProvider.getLanguages()

        lifecycleScope.launch {
            val selectedLanguage = appPreferences.getLanguage().first()
            binding?.rvLanguages?.layoutManager = LinearLayoutManager(context)
            binding?.rvLanguages?.adapter = LanguageAdapter(languages, selectedLanguage) { language ->
                onLanguageSelected(language)
                dismiss()
            }
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
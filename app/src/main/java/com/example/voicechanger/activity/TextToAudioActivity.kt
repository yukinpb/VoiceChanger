package com.example.voicechanger.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.viewModels
import com.example.voicechanger.R
import com.example.voicechanger.base.activity.BaseActivity
import com.example.voicechanger.databinding.ActivityTextToAudioBinding
import com.example.voicechanger.dialog.LanguageDialogFragment
import com.example.voicechanger.viewmodel.TextToAudioViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TextToAudioActivity : BaseActivity<ActivityTextToAudioBinding, TextToAudioViewModel>() {
    override val layoutId: Int
        get() = R.layout.activity_text_to_audio

    override fun getVM(): TextToAudioViewModel {
        val viewModel: TextToAudioViewModel by viewModels()
        return viewModel
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        setupOnBackPress()

        setupToolbar()
    }

    private fun setupToolbar() {
        customToolbar = binding.toolbar
        customToolbar?.apply {
            setToolbarTitle(getString(R.string.text_to_audio))
            setupBackButton {
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun setupOnBackPress() {
        onBackPressedDispatcher.addCallback(this) {
            binding.etTextInput.text.clear()
            getVM().resetTextToSpeech()
            finish()
        }
    }

    override fun setOnClick() {
        super.setOnClick()

        binding.apply {
            btnPlay.setOnClickListener {
                val text = etTextInput.text.toString()
                getVM().speakText(text)
            }

            btnNext.setOnClickListener {
                val text = etTextInput.text.toString()
                if (text.isNotEmpty()) {
                    val filePath = getVM().saveAudio(text)
                    startActivity(Intent(this@TextToAudioActivity, VoiceChangerActivity::class.java).apply {
                        putExtra(VoiceRecorderActivity.RECORDING_FILE_PATH, filePath)
                    })
                }
            }

            ivCountry.setOnClickListener {
                LanguageDialogFragment { language ->
                    getVM().setLanguage(language.locale)
                    ivCountry.setBackgroundResource(language.imageId)
                }.show(supportFragmentManager, "LanguageDialogFragment")
            }
        }
    }

    override fun onPause() {
        super.onPause()
        binding.etTextInput.text.clear()
        getVM().resetTextToSpeech()
    }
}
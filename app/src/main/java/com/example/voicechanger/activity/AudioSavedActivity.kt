package com.example.voicechanger.activity

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.voicechanger.R
import com.example.voicechanger.adapter.AudioFileAdapter
import com.example.voicechanger.base.activity.BaseActivityNotRequireViewModel
import com.example.voicechanger.custom.dialog.SetAsRingtoneDialog
import com.example.voicechanger.databinding.ActivityAudioSavedBinding
import com.example.voicechanger.model.AudioFile
import com.example.voicechanger.util.goToMainActivity
import com.example.voicechanger.util.goToRecordingActivity
import com.example.voicechanger.util.setOnSafeClickListener
import com.example.voicechanger.util.shareFile
import com.example.voicechanger.util.toast

class AudioSavedActivity : BaseActivityNotRequireViewModel<ActivityAudioSavedBinding>() {

    private var audioSaved: AudioFile? = null
    private var clickCount = 0

    override val layoutId: Int
        get() = R.layout.activity_audio_saved

    override fun initData() {
        super.initData()

        audioSaved = intent.getParcelableExtra(ARG_AUDIO_FILE)
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        setupToolbar()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                clickCount++
                if (clickCount == 1) {
                    this@AudioSavedActivity.toast(getString(R.string.click_one_more_time_to_go_home))
                } else if (clickCount == 2) {
                    goToMainActivity()
                }
            }
        })

        val adapter = AudioFileAdapter()
        binding.audioSaved.layoutManager = LinearLayoutManager(this)
        binding.audioSaved.adapter = adapter

        adapter.submitList(listOf(audioSaved))
    }

    private fun setupToolbar() {
        customToolbar = binding.toolbar

        customToolbar?.apply {
            setToolbarTitle(getString(R.string.saving_audio))
            setupHomeButton {
                goToMainActivity()
            }
        }
    }

    override fun setOnClick() {
        super.setOnClick()

        binding.cardShare.setOnSafeClickListener {
            audioSaved?.filePath?.let { filePath ->
                this.shareFile(filePath)
            }
        }

        binding.cardReRecord.setOnSafeClickListener {
            goToRecordingActivity()
        }

        binding.cardSetRingtone.setOnSafeClickListener {
            audioSaved?.filePath?.let { filePath ->
                val dialog = SetAsRingtoneDialog(filePath)
                dialog.show(supportFragmentManager, "SetAsRingtoneDialog")
            }
        }
    }

    companion object {
        const val ARG_AUDIO_FILE = "ARG_AUDIO_FILE"
    }
}
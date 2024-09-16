package com.example.voicechanger.activity

import android.content.Intent
import android.os.Bundle
import android.widget.SeekBar
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import com.example.voicechanger.R
import com.example.voicechanger.base.activity.BaseActivity
import com.example.voicechanger.custom.dialog.SetAsRingtoneDialog
import com.example.voicechanger.databinding.ActivityAudioPlayBinding
import com.example.voicechanger.util.goToMainActivity
import com.example.voicechanger.util.goToRecordingActivity
import com.example.voicechanger.util.setOnSafeClickListener
import com.example.voicechanger.util.toDurationString
import com.example.voicechanger.viewmodel.VoiceChangerViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class AudioPlayActivity : BaseActivity<ActivityAudioPlayBinding, VoiceChangerViewModel>() {
    override val layoutId: Int
        get() = R.layout.activity_audio_play

    override fun getVM(): VoiceChangerViewModel {
        val viewModel: VoiceChangerViewModel by viewModels()
        return viewModel
    }

    private var isPlaying = true
    private var filePath: String? = null

    override fun initData() {
        super.initData()

        filePath = intent.getStringExtra(VoiceRecorderActivity.RECORDING_FILE_PATH)
        filePath?.let {
            getVM().setTempFileName(it)
            getVM().start()
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        setupBackPress()

        setupToolbar()

        setupProgressBar()
    }

    private fun setupBackPress() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                getVM().stopMediaPlayer()
                getVM().deleteAllTempFiles()
                finish()
            }
        })
    }

    override fun setOnClick() {
        super.setOnClick()

        updateMaxDurationTextView()

        binding.btnPauseStart.setOnSafeClickListener {
            if (isPlaying) {
                getVM().pause()
                isPlaying = false
            } else {
                getVM().continuePlayback()
                isPlaying = true
            }
        }

        binding.btnSpeed.setOnSafeClickListener {
            getVM().cyclePlaybackSpeed()
        }

        binding.cardReRecord.setOnSafeClickListener {
            getVM().stopMediaPlayer()
            getVM().deleteAllTempFiles()
            goToRecordingActivity()
        }

        binding.cardSetRingtone.setOnSafeClickListener {
            filePath?.let { filePath ->
                val dialog = SetAsRingtoneDialog(filePath)
                dialog.show(supportFragmentManager, "SetAsRingtoneDialog")
            }
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()

        getVM().progress.observe(this) { progress ->
            binding.progressAudio.progress = progress
            updateCurrentTimeTextView(progress)
        }

        getVM().isPlaying.observe(this) { isPlaying ->
            if (isPlaying) {
                binding.btnPauseStart.setImageResource(R.mipmap.ic_pause_audio)
            } else {
                binding.btnPauseStart.setImageResource(R.mipmap.ic_continue_audio)
            }
        }

        getVM().playbackSpeed.observe(this) { speed ->
            binding.btnSpeed.text = getString(R.string.x, speed)
        }
    }

    private fun setupToolbar() {
        customToolbar = binding.toolbar

        customToolbar?.apply {
            setToolbarTitle(File(filePath).nameWithoutExtension)
            setupHomeButton {
                getVM().stopMediaPlayer()
                goToMainActivity()
            }
        }
    }

    private fun updateMaxDurationTextView() {
        val maxDuration = getVM().getMaxDuration()
        binding.maxTime.text = maxDuration.toDurationString()
        binding.progressAudio.max = maxDuration
    }

    private fun updateCurrentTimeTextView(progress: Int) {
        binding.currentTime.text = progress.toDurationString()
    }

    private fun setupProgressBar() {
        binding.progressAudio.progress = 0

        binding.progressAudio.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    getVM().seekTo(progress * 1000)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // No action needed
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // No action needed
            }
        })
    }
}
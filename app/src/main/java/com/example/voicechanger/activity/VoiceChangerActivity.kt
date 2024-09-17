package com.example.voicechanger.activity

import android.content.Intent
import android.os.Bundle
import android.widget.SeekBar
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import com.example.voicechanger.R
import com.example.voicechanger.adapter.VoiceChangerPagerAdapter
import com.example.voicechanger.base.activity.BaseActivity
import com.example.voicechanger.databinding.ActivityVoiceChangerBinding
import com.example.voicechanger.custom.dialog.SaveFileDialog
import com.example.voicechanger.fragment.AmbientSoundFragment
import com.example.voicechanger.fragment.SoundEffectFragment
import com.example.voicechanger.util.setOnSafeClickListener
import com.example.voicechanger.util.toDurationString
import com.example.voicechanger.viewmodel.VoiceChangerViewModel
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VoiceChangerActivity : BaseActivity<ActivityVoiceChangerBinding, VoiceChangerViewModel>() {

    private var isPlaying = true

    override val layoutId: Int
        get() = R.layout.activity_voice_changer

    override fun getVM(): VoiceChangerViewModel {
        val viewModel: VoiceChangerViewModel by viewModels()
        return viewModel
    }

    override fun initData() {
        super.initData()

        val fileName = intent.getStringExtra(VoiceRecorderActivity.RECORDING_FILE_PATH)
        fileName?.let {
            getVM().setTempFileName(it)
            getVM().start()
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        setupBackPress()

        setupToolbar()

        setupViewPager()

        setupProgressBar()
    }

    private fun setupViewPager() {
        val fragments = listOf(
                SoundEffectFragment.newInstance(),
        AmbientSoundFragment.newInstance()
        )

        binding.viewPager.adapter = VoiceChangerPagerAdapter(this, fragments)

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.sound_effect)
                1 -> getString(R.string.ambient_sound)
                else -> null
            }
        }.attach()
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

        binding.btnVolume.setOnSafeClickListener {
            getVM().toggleVolume()
        }

        binding.btnReset.setOnSafeClickListener {
            getVM().reset()
            binding.progressAudio.progress = 0
            binding.currentTime.text = getString(R.string.time_start_audio)
        }

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
    }

    override fun bindingStateView() {
        super.bindingStateView()

        getVM().progress.observe(this) { progress ->
            binding.progressAudio.progress = progress
            updateCurrentTimeTextView(progress)
        }

        getVM().isVolumeOn.observe(this) { isVolumeOn ->
            if (isVolumeOn) {
                binding.btnVolume.setImageResource(R.mipmap.ic_turn_on_volume)
            } else {
                binding.btnVolume.setImageResource(R.mipmap.ic_turn_off_volume)
            }
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

        getVM().maxDuration.observe(this) { maxDuration ->
            binding.maxTime.text = maxDuration.toDurationString()
            binding.progressAudio.max = maxDuration
        }
    }

    private fun setupToolbar() {
        customToolbar = binding.toolbar

        customToolbar?.apply {
            setToolbarTitle(getString(R.string.voice_changer))
            setupOkButton {
                showEnterFileNameDialog()
            }
            setupBackButton {
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun showEnterFileNameDialog() {
        SaveFileDialog(
            onClickOK = { fileName ->
                getVM().stopMediaPlayer()
                getVM().setFinalFileName(fileName)
                getVM().saveAudio()
                getVM().deleteAllTempFiles()
                goToAudioSaved()
            }
        ).show(supportFragmentManager, SaveFileDialog::class.java.simpleName)
    }

    private fun goToAudioSaved() {
        val intent = Intent(this, AudioSavedActivity::class.java).apply {
            putExtra(AudioSavedActivity.ARG_AUDIO_FILE, getVM().getAudioSaved())
        }
        startActivity(intent)
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
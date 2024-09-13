package com.example.voicechanger.activity

import android.os.Bundle
import android.widget.SeekBar
import androidx.activity.viewModels
import com.example.voicechanger.R
import com.example.voicechanger.adapter.VoiceChangerPagerAdapter
import com.example.voicechanger.base.activity.BaseActivity
import com.example.voicechanger.custom.toolbar.CustomToolbar
import com.example.voicechanger.databinding.ActivityVoiceChangerBinding
import com.example.voicechanger.dialog.EnterFileNameDialog
import com.example.voicechanger.fragment.VoiceChangerFragment
import com.example.voicechanger.util.Constants
import com.example.voicechanger.util.SoundEffectProvider
import com.example.voicechanger.viewmodel.VoiceChangerViewModel
import com.google.android.material.tabs.TabLayoutMediator

class VoiceChangerActivity : BaseActivity<ActivityVoiceChangerBinding, VoiceChangerViewModel>() {

    private var isPlaying = true
    override val layoutId: Int
        get() = R.layout.activity_voice_changer

    override fun getVM(): VoiceChangerViewModel {
        val viewModel: VoiceChangerViewModel by viewModels()
        return viewModel
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        val fileName = intent.getStringExtra(VoiceRecorderActivity.RECORDING_FILE_PATH)
        fileName?.let {
            getVM().setTempFileName(it)
        }

        customToolbar = CustomToolbar(this).apply {
            setToolbarTitle(getString(R.string.voice_changer))
            setOkButtonVisibility(true)
            setOkButtonClickListener {
                showEnterFileNameDialog()
            }
        }

        binding.toolbar.addView(customToolbar)

        val listItemSoundEffect = SoundEffectProvider.getSoundEffectItems()

        val fragments = listOf(
            VoiceChangerFragment.newInstance(Constants.SOUND_EFFECT, listItemSoundEffect),
            VoiceChangerFragment.newInstance(Constants.AMBIENT_SOUND, emptyList())
        )

        binding.viewPager.adapter = VoiceChangerPagerAdapter(this, fragments)

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.sound_effect)
                1 -> getString(R.string.ambient_sound)
                else -> null
            }
        }.attach()

        setupProgressBar()
    }

    override fun setOnClick() {
        super.setOnClick()

        updateMaxDurationTextView()

        binding.btnVolume.setOnClickListener {
            getVM().toggleVolume()
        }

        binding.btnReset.setOnClickListener {
            getVM().reset()
            binding.progressAudio.progress = 0
            binding.currentTime.text = getString(R.string.time_start_audio)
        }

        binding.btnPauseStart.setOnClickListener {
            if (isPlaying) {
                getVM().pause()
                isPlaying = false
            } else {
                getVM().continuePlayback()
                isPlaying = true
            }
        }

        binding.btnSpeed.setOnClickListener {
            getVM().cyclePlaybackSpeed()
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()

        getVM().progress.observe(this) { progress ->
            binding.progressAudio.progress = progress
            updateCurrentTimeTextView()
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
    }

    private fun showEnterFileNameDialog() {
        EnterFileNameDialog(
            onClickOK = { fileName ->
                getVM().setFinalFileName(fileName)
                getVM().saveAudio()

            }
        ).show(supportFragmentManager, EnterFileNameDialog::class.java.simpleName)
    }

    private fun updateMaxDurationTextView() {
        val maxDuration = getVM().getMaxDuration()
        binding.maxTime.text = maxDuration
    }

    private fun updateCurrentTimeTextView() {
        val currentTime = getVM().getCurrentPlaybackTime()
        binding.currentTime.text = currentTime
    }

    private fun setupProgressBar() {
        binding.progressAudio.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    getVM().seekTo(progress)
                    updateCurrentTimeTextView()
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
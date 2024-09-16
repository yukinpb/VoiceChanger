package com.example.voicechanger.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.voicechanger.R
import com.example.voicechanger.base.activity.BaseActivity
import com.example.voicechanger.databinding.ActivityVoiceRecorderBinding
import com.example.voicechanger.util.goToMainActivity
import com.example.voicechanger.util.setOnSafeClickListener
import com.example.voicechanger.viewmodel.VoiceRecorderViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class VoiceRecorderActivity : BaseActivity<ActivityVoiceRecorderBinding, VoiceRecorderViewModel>() {

    private lateinit var rotateAnimation: Animation
    private var isRecording = false
    private var isPaused = false

    override val layoutId: Int
        get() = R.layout.activity_voice_recorder

    private val permissions =
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.RECORD_AUDIO)
        } else {
            arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }

    override fun getVM(): VoiceRecorderViewModel {
        val viewModel: VoiceRecorderViewModel by viewModels()
        return viewModel
    }

    private fun checkPermissions(): Boolean {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == -1 ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == -1 ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == -1
        ) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION)
        }
        return permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        checkPermissions()

        setupToolbar()

        setupBackPress()

        Glide.with(this)
            .load(R.mipmap.img_recorder)
            .circleCrop()
            .into(binding.imgRecorder)

        rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_infinite)

        binding.btnStop.isEnabled = false
    }

    private fun setupBackPress() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                getVM().stopRecording()
                goToMainActivity()
            }
        })
    }

    override fun setOnClick() {
        super.setOnClick()

        binding.btnStartPauseRecord.setOnSafeClickListener {
            when {
                isRecording && !isPaused -> {
                    getVM().pauseRecording()
                    binding.btnStartPauseRecord.setBackgroundResource(R.mipmap.ic_continue_record)
                    binding.tapToRecordHint.text = getString(R.string.tap_to_continue)
                    isPaused = true
                    binding.imgRecorder.clearAnimation()
                }

                isPaused -> {
                    getVM().continueRecording()
                    binding.btnStartPauseRecord.setBackgroundResource(R.mipmap.ic_pause_record)
                    binding.tapToRecordHint.text = getString(R.string.tap_to_pause)
                    isPaused = false
                    binding.imgRecorder.startAnimation(rotateAnimation)
                }

                else -> {
                    getVM().startRecording()
                    binding.btnStartPauseRecord.setBackgroundResource(R.mipmap.ic_pause_record)
                    binding.tapToRecordHint.text = getString(R.string.tap_to_pause)
                    isRecording = true
                    binding.imgRecorder.startAnimation(rotateAnimation)
                    binding.btnStop.isEnabled = true
                }
            }
        }

        binding.btnReset.setOnSafeClickListener {
            getVM().resetRecording()
            resetUI()
        }

        binding.btnStop.setOnSafeClickListener {
            getVM().stopRecording()
            startActivity(Intent(this, VoiceChangerActivity::class.java).apply {
                putExtra(RECORDING_FILE_PATH, getVM().getRecordingFilePath())
            })
            CoroutineScope(Dispatchers.Main).launch {
                delay(1000)
                resetUI()
            }
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()

        getVM().timerText.observe(this) { time ->
            binding.timerText.text = time
        }

        getVM().volumeLevel.observe(this) { volume ->
            binding.customVolumeCircleView.updateVolumeLevel(volume)
        }
    }

    private fun setupToolbar() {
        customToolbar = binding.toolbar
        customToolbar?.apply {
            setToolbarTitle(getString(R.string.voice_changer))
            setupBackButton {
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun resetUI() {
        binding.btnStartPauseRecord.setBackgroundResource(R.mipmap.ic_start_record)
        binding.tapToRecordHint.text = getString(R.string.tap_to_start_recording)
        binding.timerText.text = getString(R.string.timer_start)
        isRecording = false
        isPaused = false
        binding.btnStop.isEnabled = false
        binding.imgRecorder.clearAnimation()
    }

    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
        const val RECORDING_FILE_PATH = "RECORDING_FILE_PATH "
    }
}
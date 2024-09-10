package com.example.voicechanger.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.voicechanger.R
import com.example.voicechanger.databinding.ActivityVoiceRecorderBinding
import com.example.voicechanger.viewmodel.VoiceRecorderViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VoiceRecorderActivity : AppCompatActivity() {

    private val viewModel: VoiceRecorderViewModel by viewModels()
    private lateinit var binding: ActivityVoiceRecorderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVoiceRecorderBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        observe()
        setListeners()
    }

    private fun setListeners() {
        binding.btRecord.setOnClickListener {
            if (viewModel.isRecording.value == true) {
                viewModel.stopRecording()
            } else {
                viewModel.startRecording()
            }
        }

        binding.ivNext.setOnClickListener {
             startActivity(Intent(this, VoiceChangerActivity::class.java))
        }
    }

    private fun observe() {
        viewModel.requestAudioPermissions(this)

        viewModel.isRecording.observe(this) { isRecording ->
            binding.btRecord.text = if (isRecording) getString(R.string.stop) else getString(R.string.record)
            binding.ivNext.visibility = if (isRecording) View.GONE else View.VISIBLE
        }

        viewModel.timerText.observe(this) { timerText ->
            binding.tvTimer.text = timerText
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.stopRecording()
    }
}
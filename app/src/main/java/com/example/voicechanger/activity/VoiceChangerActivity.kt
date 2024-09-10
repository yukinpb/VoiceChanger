package com.example.voicechanger.activity

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.voicechanger.R
import com.example.voicechanger.databinding.ActivityVoiceChangerBinding
import com.example.voicechanger.viewmodel.VoiceChangerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VoiceChangerActivity : AppCompatActivity() {

    private val viewModel: VoiceChangerViewModel by viewModels()
    private lateinit var binding: ActivityVoiceChangerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVoiceChangerBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel.isProgressVisible.observe(this) { isVisible ->
            binding.progressCircular.visibility = if (isVisible) View.VISIBLE else View.GONE
        }

        setListeners()
    }

    private fun setListeners() {
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.ivChipmunk.setOnClickListener {
            viewModel.playChipmunk()
        }

        binding.ivRobot.setOnClickListener {
            viewModel.playRobot()
        }

        binding.ivRadio.setOnClickListener {
            viewModel.playRadio()
        }

        binding.ivCave.setOnClickListener {
            viewModel.playCave()
        }
    }
}
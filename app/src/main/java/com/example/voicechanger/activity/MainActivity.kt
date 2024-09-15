package com.example.voicechanger.activity

import com.example.voicechanger.service.FileCleanupService
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.voicechanger.R
import com.example.voicechanger.adapter.AudioFileAdapter
import com.example.voicechanger.base.activity.BaseActivity
import com.example.voicechanger.custom.toolbar.CustomToolbar
import com.example.voicechanger.databinding.ActivityMainBinding
import com.example.voicechanger.viewmodel.MainViewModel

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {
    private lateinit var adapter: AudioFileAdapter

    override val layoutId: Int
        get() = R.layout.activity_main

    override fun getVM(): MainViewModel {
        val viewModel: MainViewModel by viewModels()
        return viewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        startService(Intent(this, FileCleanupService::class.java))

        super.onCreate(savedInstanceState)
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        customToolbar = CustomToolbar(this).apply {
            setToolbarTitle(context.getString(R.string.voice_changer))
            setUpSettingButton {

            }
        }

        binding.toolbar.addView(customToolbar)

        adapter = AudioFileAdapter()
        binding.rvMyRecord.layoutManager = LinearLayoutManager(this)
        binding.rvMyRecord.adapter = adapter
    }

    override fun setOnClick() {
        super.setOnClick()

        binding.btnRecord.setOnClickListener {
            startActivity(Intent(this, VoiceRecorderActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()

        getVM().loadAudioFiles()
    }

    override fun bindingStateView() {
        super.bindingStateView()

        getVM().audioFiles.observe(this) { audioFiles ->
            if (audioFiles.isEmpty()) {
                binding.llNoRecord.visibility = View.VISIBLE
                binding.rvMyRecord.visibility = View.GONE
            } else {
                binding.llNoRecord.visibility = View.GONE
                binding.rvMyRecord.visibility = View.VISIBLE
                adapter.submitList(audioFiles)
            }
        }
    }
}
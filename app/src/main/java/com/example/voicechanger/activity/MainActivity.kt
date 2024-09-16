package com.example.voicechanger.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.voicechanger.R
import com.example.voicechanger.adapter.AudioFileAdapter
import com.example.voicechanger.base.activity.BaseActivity
import com.example.voicechanger.databinding.ActivityMainBinding
import com.example.voicechanger.service.FileCleanupService
import com.example.voicechanger.util.Constants
import com.example.voicechanger.util.setOnSafeClickListener
import com.example.voicechanger.util.toast
import com.example.voicechanger.viewmodel.MainViewModel

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {
    private lateinit var adapter: AudioFileAdapter
    private var clickCount = 0

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

    override fun initData() {
        super.initData()

        getVM().loadAudioFiles()
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        setupToolbar()

        setupBackPress()

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        adapter = AudioFileAdapter(
            isShowMenu = false,
            onItemClicked = { audioFile ->
                startActivity(Intent(this, AudioPlayActivity::class.java).apply {
                    putExtra(VoiceRecorderActivity.RECORDING_FILE_PATH, audioFile.filePath)
                })
            }
        )
        binding.rvMyRecord.layoutManager = LinearLayoutManager(this)
        binding.rvMyRecord.adapter = adapter
    }

    private fun setupBackPress() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                clickCount++
                if (clickCount == 1) {
                    this@MainActivity.toast(getString(R.string.click_one_more_time_to_exit))
                } else if (clickCount == 2) {
                    finish()
                }
            }
        })
    }

    override fun setOnClick() {
        super.setOnClick()

        binding.btnRecord.setOnSafeClickListener {
            startActivity(Intent(this, VoiceRecorderActivity::class.java))
        }

        binding.tvSeeAll.setOnSafeClickListener {
            openAudioList(Constants.Directories.VOICE_CHANGER_DIR)
        }

        binding.btnUploadFile.setOnSafeClickListener {
            openAudioList(Constants.Directories.VOICE_RECORDER_DIR)
        }
    }

    override fun onResume() {
        super.onResume()

        getVM().loadAudioFiles()
    }

    private fun setupToolbar() {
        customToolbar = binding.toolbar

        customToolbar?.apply {
            setToolbarTitle(context.getString(R.string.voice_changer))
            setupMenuButton(false)
            setupBackButton(false)
            setupSettingButton {

            }
        }
    }

    private fun openAudioList(directory: String) {
        val intent = Intent(this, AudioListActivity::class.java)
        intent.putExtra(AudioListActivity.ARG_DIRECTORY, directory)
        startActivity(intent)
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
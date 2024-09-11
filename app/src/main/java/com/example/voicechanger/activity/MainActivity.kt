package com.example.voicechanger.activity

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.voicechanger.R
import androidx.activity.viewModels
import com.example.voicechanger.databinding.ActivityMainBinding
import com.example.voicechanger.adapter.AudioFileAdapter
import com.example.voicechanger.base.activity.BaseActivity
import com.example.voicechanger.custom.toolbar.CustomToolbar
import com.example.voicechanger.viewmodel.MainViewModel

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {
    private lateinit var customToolbar: CustomToolbar
    private lateinit var adapter: AudioFileAdapter

    override val layoutId: Int
        get() = R.layout.activity_main

    override fun getVM(): MainViewModel {
        val viewModel: MainViewModel by viewModels()
        return viewModel
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        customToolbar = CustomToolbar(this).apply {
            setToolbarTitle(context.getString(R.string.voice_changer))
            setSettingButtonVisibility(true)
        }

        binding.toolbar.addView(customToolbar)

        adapter = AudioFileAdapter()
        binding.rvMyRecord.layoutManager = LinearLayoutManager(this)
        binding.rvMyRecord.adapter = adapter
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
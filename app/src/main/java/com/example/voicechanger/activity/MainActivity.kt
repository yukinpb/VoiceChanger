package com.example.voicechanger.activity

import com.example.voicechanger.service.FileCleanupService
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.voicechanger.R
import com.example.voicechanger.adapter.AudioFileAdapter
import com.example.voicechanger.base.activity.BaseActivity
import com.example.voicechanger.custom.toolbar.CustomToolbar
import com.example.voicechanger.custom.view.SortAudioMenu
import com.example.voicechanger.databinding.ActivityMainBinding
import com.example.voicechanger.fragment.AudioListFragment
import com.example.voicechanger.util.Constants
import com.example.voicechanger.util.SortType
import com.example.voicechanger.util.setOnSafeClickListener
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

    override fun initData() {
        super.initData()

        getVM().loadAudioFiles()
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        setUpToolbar()

        adapter = AudioFileAdapter()
        binding.rvMyRecord.layoutManager = LinearLayoutManager(this)
        binding.rvMyRecord.adapter = adapter
    }

    override fun setOnClick() {
        super.setOnClick()

        binding.btnRecord.setOnSafeClickListener {
            startActivity(Intent(this, VoiceRecorderActivity::class.java))
        }

        binding.tvSeeAll.setOnSafeClickListener {
            openAudioListFragment(Constants.Directories.VOICE_CHANGER_DIR)
        }

        binding.btnUploadFile.setOnSafeClickListener {
            openAudioListFragment(Constants.Directories.VOICE_RECORDER_DIR)
        }
    }

    override fun onResume() {
        super.onResume()

        getVM().loadAudioFiles()
    }

    private fun setUpToolbar() {
        customToolbar = binding.toolbar

        customToolbar?.apply {
            setToolbarTitle(context.getString(R.string.voice_changer))
            setUpSettingButton {

            }
        }
    }

    private fun openAudioListFragment(directory: String) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
            .replace(R.id.fragmentContainer, AudioListFragment.newInstance(directory))
            .addToBackStack(null)
            .commit()

        customToolbar?.apply {
            setToolbarTitle(
                if (directory == Constants.Directories.VOICE_CHANGER_DIR) getString(R.string.my_record) else getString(
                    R.string.upload_files
                )
            )
            setUpSettingButton(false)
            setUpMenuButton {
                showSortAudioMenu()
            }
        }
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

    private fun showSortAudioMenu() {
        val sortAudioMenu = SortAudioMenu(
            onSortNewest = {
                if (getVM().getSortType() == SortType.DATE_NEWEST) {
                    getVM().setSortType(SortType.DATE_OLDEST)
                } else {
                    getVM().setSortType(SortType.DATE_NEWEST)
                }
            },
            onSortFileName = {
                if (getVM().getSortType() == SortType.NAME_ASC) {
                    getVM().setSortType(SortType.NAME_DESC)
                } else {
                    getVM().setSortType(SortType.NAME_ASC)
                }
            }
        )

        sortAudioMenu.dialog?.window?.apply {
            val layoutParams = attributes
            layoutParams.y = 100
            layoutParams.x = -50
            attributes = layoutParams
        }
    }
}
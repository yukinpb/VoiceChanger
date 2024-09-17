package com.example.voicechanger.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.PopupWindow
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.voicechanger.R
import com.example.voicechanger.activity.VoiceRecorderActivity.Companion.RECORDING_FILE_PATH
import com.example.voicechanger.adapter.AudioFileAdapter
import com.example.voicechanger.base.activity.BaseActivity
import com.example.voicechanger.custom.dialog.ConfirmDialog
import com.example.voicechanger.custom.dialog.SaveFileDialog
import com.example.voicechanger.custom.dialog.SetAsRingtoneDialog
import com.example.voicechanger.custom.view.AudioFileOptionsPopup
import com.example.voicechanger.custom.view.AudioFileSortPopup
import com.example.voicechanger.databinding.ActivityAudioListBinding
import com.example.voicechanger.model.AudioFile
import com.example.voicechanger.util.Constants
import com.example.voicechanger.util.SortType
import com.example.voicechanger.util.shareFile
import com.example.voicechanger.util.toast
import com.example.voicechanger.viewmodel.MainViewModel
import java.io.File

class AudioListActivity : BaseActivity<ActivityAudioListBinding, MainViewModel>() {

    private lateinit var adapter: AudioFileAdapter
    private var audioFiles: List<AudioFile> = listOf()
    private var currentPopup: PopupWindow? = null
    private val directory: String by lazy {
        intent.getStringExtra(ARG_DIRECTORY) ?: Constants.Directories.VOICE_CHANGER_DIR
    }

    override val layoutId: Int
        get() = R.layout.activity_audio_list

    override fun getVM(): MainViewModel {
        val viewModel: MainViewModel by viewModels()
        return viewModel
    }

    override fun initData() {
        super.initData()

        val directory =
            intent.getStringExtra(ARG_DIRECTORY) ?: Constants.Directories.VOICE_CHANGER_DIR
        getVM().loadAudioFiles(directory)
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        setupToolbar()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })

        adapter = AudioFileAdapter(
            isShowMenu = true,
            onMenuClick = { anchorView, audioFile ->
                showMenu(anchorView, audioFile)
            },
            onItemClicked = { audioFile ->
                if (directory == Constants.Directories.VOICE_CHANGER_DIR) {
                    startActivity(Intent(this, AudioPlayActivity::class.java).apply {
                        putExtra(RECORDING_FILE_PATH, audioFile.filePath)
                    })
                } else {
                    startActivity(Intent(this, VoiceChangerActivity::class.java).apply {
                        putExtra(RECORDING_FILE_PATH, audioFile.filePath)
                    })
                }
            }
        )
        binding.rvAudioList.layoutManager = LinearLayoutManager(this)
        binding.rvAudioList.adapter = adapter

        binding.searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filter(newText ?: "")
                return true
            }
        })
    }

    private fun setupToolbar() {
        customToolbar = binding.toolbar

        customToolbar?.apply {
            setToolbarTitle(
                if (directory == Constants.Directories.VOICE_CHANGER_DIR) getString(R.string.my_record) else getString(
                    R.string.upload_files
                )
            )
            setupSettingButton(false)
            setupMenuButton {
                showSortAudioMenu(binding.toolbar)
            }
            setupBackButton {
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun showSortAudioMenu(anchorView: View) {
        currentPopup?.dismiss()
        if (currentPopup?.isShowing == true) {
            currentPopup = null
            return
        }

        val audioFileSortPopup = AudioFileSortPopup(
            context = this,
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

        audioFileSortPopup.show(anchorView)
        currentPopup = audioFileSortPopup
    }

    private fun showMenu(anchorView: View, audioFile: AudioFile) {
        currentPopup?.dismiss()
        if (currentPopup?.isShowing == true) {
            currentPopup = null
            return
        }

        val audioFileOptionsPopup = AudioFileOptionsPopup(
            context = this,
            onRename = {
                showEnterFileNameDialog(audioFile)
            },
            onSetAsRingtone = {
                showSetAsRingtoneDialog(audioFile)
            },
            onShare = {
                onShareAudioFile(audioFile)
            },
            onDelete = {
                onDeleteAudioFile(audioFile)
            }
        )

        audioFileOptionsPopup.show(anchorView)
        currentPopup = audioFileOptionsPopup
    }

    private fun filter(query: String) {
        val filteredList = audioFiles.filter { it.name.contains(query, ignoreCase = true) }
        if (filteredList.isEmpty()) {
            binding.llNoRecord.visibility = View.VISIBLE
            binding.rvAudioList.visibility = View.GONE
        } else {
            binding.llNoRecord.visibility = View.GONE
            binding.rvAudioList.visibility = View.VISIBLE
            adapter.submitList(filteredList)
        }
    }

    private fun onShareAudioFile(audioFile: AudioFile) {
        audioFile.filePath.let { filePath ->
            this.shareFile(filePath)
        }
    }

    private fun onDeleteAudioFile(audioFile: AudioFile) {
        ConfirmDialog(
            title = getString(R.string.confirm_delete_title),
            content = getString(R.string.confirm_delete_content, audioFile.name),
            onClickOK = {
                val file = File(audioFile.filePath)
                if (file.delete()) {
                    this.toast(getString(R.string.deleted_file_successfully, audioFile.name))
                    getVM().loadAudioFiles(directory)
                }
            }
        ).show(supportFragmentManager, ConfirmDialog::class.java.simpleName)
    }

    private fun showSetAsRingtoneDialog(audioFile: AudioFile) {
        val dialog = SetAsRingtoneDialog(audioFile.filePath)
        dialog.show(supportFragmentManager, "SetAsRingtoneDialog")
    }

    private fun showEnterFileNameDialog(audioFile: AudioFile) {
        SaveFileDialog(
            onClickOK = { fileName ->
                val file = File(audioFile.filePath)
                val newFile = File(file.parent, "$fileName.mp3")
                if (file.renameTo(newFile)) {
                    this.toast(
                        getString(
                            R.string.rename_file_successfully,
                            audioFile.name,
                            fileName
                        )
                    )
                    getVM().loadAudioFiles(directory)
                }
            }
        ).show(supportFragmentManager, SaveFileDialog::class.java.simpleName)
    }

    override fun bindingStateView() {
        super.bindingStateView()

        getVM().audioFiles.observe(this) { audioFiles ->
            if (audioFiles.isEmpty()) {
                binding.llNoRecord.visibility = View.VISIBLE
                binding.rvAudioList.visibility = View.GONE
            } else {
                binding.llNoRecord.visibility = View.GONE
                binding.rvAudioList.visibility = View.VISIBLE
                this.audioFiles = audioFiles
                adapter.submitList(audioFiles)
            }
        }
    }

    companion object {
        const val ARG_DIRECTORY = "directory"
    }
}
package com.example.voicechanger.fragment

import android.content.Intent
import android.os.Bundle
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.voicechanger.R
import com.example.voicechanger.adapter.AudioFileAdapter
import com.example.voicechanger.base.fragment.BaseFragment
import com.example.voicechanger.custom.dialog.SetAsRingtoneDialog
import com.example.voicechanger.databinding.FragmentAudioSavedBinding
import com.example.voicechanger.model.AudioFile
import com.example.voicechanger.util.setOnSafeClickListener
import com.example.voicechanger.viewmodel.VoiceChangerViewModel
import java.io.File

class AudioSavedFragment :
    BaseFragment<FragmentAudioSavedBinding, VoiceChangerViewModel>(R.layout.fragment_audio_saved) {

    private lateinit var audioSaved: AudioFile

    override fun getVM(): VoiceChangerViewModel {
        val viewModel: VoiceChangerViewModel by viewModels({ requireActivity() })
        return viewModel
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        audioSaved = getVM().getAudioSaved()

        val adapter = AudioFileAdapter()
        binding.audioSaved.layoutManager = LinearLayoutManager(context)
        binding.audioSaved.adapter = adapter

        adapter.submitList(listOf(audioSaved))
    }

    override fun setOnClick() {
        super.setOnClick()

        binding.cardShare.setOnSafeClickListener {
            audioSaved.filePath?.let {
                shareFile(it)
            }
        }

        binding.cardReRecord.setOnSafeClickListener {
            requireActivity().finish()
        }

        binding.cardSetRingtone.setOnSafeClickListener {
            val audioFile = getVM().getAudioSaved()
            audioFile.filePath?.let { filePath ->
                val dialog = SetAsRingtoneDialog(filePath)
                dialog.show(parentFragmentManager, "SetAsRingtoneDialog")
            }
        }
    }

    private fun shareFile(filePath: String) {
        val file = File(filePath)
        val uri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            file
        )
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "audio/*"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_audio)))
    }
}
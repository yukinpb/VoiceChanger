package com.example.voicechanger.fragment

import android.os.Bundle
import android.widget.SeekBar
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.voicechanger.R
import com.example.voicechanger.adapter.VoiceChangerAdapter
import com.example.voicechanger.base.fragment.BaseFragment
import com.example.voicechanger.databinding.FragmentAmbientSoundBinding
import com.example.voicechanger.model.VoiceChangerItem
import com.example.voicechanger.util.AmbientSoundProvider
import com.example.voicechanger.viewmodel.VoiceChangerViewModel
import java.io.File

class AmbientSoundFragment :
    BaseFragment<FragmentAmbientSoundBinding, VoiceChangerViewModel>(R.layout.fragment_ambient_sound) {

    private lateinit var adapter: VoiceChangerAdapter
    private var dataList: List<VoiceChangerItem>? = null

    override fun getVM(): VoiceChangerViewModel {
        val viewModel: VoiceChangerViewModel by viewModels({ requireActivity() })
        return viewModel
    }

    companion object {
        fun newInstance(): AmbientSoundFragment {
            val fragment = AmbientSoundFragment()
            return fragment
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        binding.recyclerView.layoutManager =
            GridLayoutManager(context, 2, LinearLayoutManager.VERTICAL, false)
        adapter = VoiceChangerAdapter {
            mergeWithBackground(it.id, binding.slider.progress)
        }
        binding.recyclerView.adapter = adapter

        binding.slider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.number.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        dataList = AmbientSoundProvider.getAmbientSoundItems()
        dataList?.let {
            adapter.submitList(it)
        }
    }

    private fun mergeWithBackground(backgroundResId: Int, backgroundVolume: Int) {
        if (backgroundResId == 0) {
            getVM().mergeWithBackground("", 0)
        } else {
            val inputStream = resources.openRawResource(backgroundResId)
            val tempFile = File(context?.cacheDir, "temp_background.mp3")
            tempFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            val backgroundFileName = tempFile.absolutePath
            getVM().mergeWithBackground(backgroundFileName, backgroundVolume)
        }
    }
}
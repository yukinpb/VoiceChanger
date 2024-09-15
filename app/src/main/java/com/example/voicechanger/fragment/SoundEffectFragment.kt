package com.example.voicechanger.fragment

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.voicechanger.R
import com.example.voicechanger.adapter.VoiceChangerAdapter
import com.example.voicechanger.base.fragment.BaseFragment
import com.example.voicechanger.databinding.FragmentSoundEffectBinding
import com.example.voicechanger.model.VoiceChangerItem
import com.example.voicechanger.util.SoundEffectProvider
import com.example.voicechanger.viewmodel.VoiceChangerViewModel

class SoundEffectFragment :
    BaseFragment<FragmentSoundEffectBinding, VoiceChangerViewModel>(R.layout.fragment_sound_effect) {

    private lateinit var adapter: VoiceChangerAdapter
    private var dataList: List<VoiceChangerItem>? = null

    override fun getVM(): VoiceChangerViewModel {
        val viewModel: VoiceChangerViewModel by viewModels({ requireActivity() })
        return viewModel
    }

    companion object {
        fun newInstance(): SoundEffectFragment {
            val fragment = SoundEffectFragment()
            return fragment
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        binding.recyclerView.layoutManager =
            GridLayoutManager(context, 2, LinearLayoutManager.VERTICAL, false)
        adapter = VoiceChangerAdapter {
            getVM().applyEffect(it.id)
        }
        binding.recyclerView.adapter = adapter

        dataList = SoundEffectProvider.getSoundEffectItems()
        dataList?.let {
            adapter.submitList(it)
        }
    }
}
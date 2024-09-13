package com.example.voicechanger.fragment

import android.os.Build
import android.os.Bundle
import android.widget.SeekBar
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.voicechanger.R
import com.example.voicechanger.adapter.VoiceChangerAdapter
import com.example.voicechanger.base.fragment.BaseFragment
import com.example.voicechanger.databinding.FragmentVoiceChangerBinding
import com.example.voicechanger.model.VoiceChangerItem
import com.example.voicechanger.util.Constants
import com.example.voicechanger.viewmodel.VoiceChangerViewModel

class VoiceChangerFragment :
    BaseFragment<FragmentVoiceChangerBinding, VoiceChangerViewModel>(R.layout.fragment_voice_changer) {

    private lateinit var adapter: VoiceChangerAdapter
    private var category: String? = null
    private var dataList: List<VoiceChangerItem>? = null

    override fun getVM(): VoiceChangerViewModel {
        val viewModel: VoiceChangerViewModel by viewModels({ requireParentFragment() })
        return viewModel
    }

    companion object {
        private const val ARG_CATEGORY = "category"
        private const val ARG_DATA_LIST = "data_list"

        fun newInstance(category: String, dataList: List<VoiceChangerItem>): VoiceChangerFragment {
            val fragment = VoiceChangerFragment()
            val args = Bundle()
            args.putString(ARG_CATEGORY, category)
            args.putParcelableArrayList(ARG_DATA_LIST, ArrayList(dataList))
            fragment.arguments = args
            return fragment
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        arguments?.let {
            category = it.getString(ARG_CATEGORY)
            dataList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.getParcelableArrayList(ARG_DATA_LIST, VoiceChangerItem::class.java)
            } else {
                it.getParcelableArrayList(ARG_DATA_LIST)
            }
        }

        dataList?.let {
            adapter.submitList(it)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = VoiceChangerAdapter {
            getVM().applyEffect(it.id)
        }
        binding.recyclerView.adapter = adapter

        binding.slider.max = 100
        binding.slider.progress = getVM().getCurrentPitch()

        binding.slider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                when (category) {
                    Constants.SOUND_EFFECT -> getVM().setPitch(progress)
                    Constants.AMBIENT_SOUND -> {}
                    else -> getVM().setPitch(progress - 50)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.title.text = when (category) {
            Constants.SOUND_EFFECT -> getString(R.string.pitch)
            Constants.AMBIENT_SOUND -> getString(R.string.sound)
            else -> getString(R.string.pitch)
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()

        getVM().pitch.observe(viewLifecycleOwner) {
            binding.slider.progress = it + 50
        }
    }
}
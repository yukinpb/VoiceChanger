package com.example.voicechanger.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.voicechanger.R
import com.example.voicechanger.adapter.AudioFileAdapter
import com.example.voicechanger.base.fragment.BaseFragment
import com.example.voicechanger.databinding.FragmentAudioListBinding
import com.example.voicechanger.model.AudioFile
import com.example.voicechanger.util.Constants
import com.example.voicechanger.util.SortType
import com.example.voicechanger.viewmodel.MainViewModel

class AudioListFragment : BaseFragment<FragmentAudioListBinding, MainViewModel>(R.layout.fragment_audio_list) {

    private lateinit var adapter: AudioFileAdapter
    private var audioFiles: List<AudioFile> = listOf()

    override fun getVM(): MainViewModel {
        val viewModel: MainViewModel by viewModels({ requireActivity() })
        return viewModel
    }

    override fun initData() {
        super.initData()

        val directory = arguments?.getString(ARG_DIRECTORY) ?: Constants.Directories.VOICE_CHANGER_DIR
        getVM().loadAudioFiles(directory)
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        adapter = AudioFileAdapter()
        binding.rvAudioList.layoutManager = LinearLayoutManager(context)
        binding.rvAudioList.adapter = adapter

        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filter(newText ?: "")
                return true
            }
        })

    }

    private fun filter(query: String) {
        val filteredList = audioFiles.filter { it.name.contains(query, ignoreCase = true) }
        adapter.submitList(filteredList)
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
        private const val ARG_DIRECTORY = "directory"

        fun newInstance(directory: String): AudioListFragment {
            val fragment = AudioListFragment()
            val args = Bundle()
            args.putString(ARG_DIRECTORY, directory)
            fragment.arguments = args
            return fragment
        }
    }
}
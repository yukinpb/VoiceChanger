package com.example.voicechanger.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.voicechanger.R
import com.example.voicechanger.databinding.ItemAudioFileBinding
import com.example.voicechanger.model.AudioFile

class AudioFileAdapter(
    private val isShowMenu: Boolean = false,
    private val onMenuClick: (View, AudioFile) -> Unit = { _, _ -> },
    private val onItemClicked: (AudioFile) -> Unit = {}
) : ListAdapter<AudioFile, AudioFileAdapter.AudioFileViewHolder>(AudioFileDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioFileViewHolder {
        val binding = ItemAudioFileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AudioFileViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AudioFileViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class AudioFileViewHolder(private val binding: ItemAudioFileBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(audioFile: AudioFile) {
            binding.nameAudio.text = audioFile.name
            binding.infoAudio.text = binding.root.context.getString(R.string.audio_info, audioFile.time, audioFile.size)
            binding.ivMenu.isVisible = isShowMenu
            binding.ivMenu.setOnClickListener { onMenuClick(binding.ivMenu, audioFile) }
            binding.root.setOnClickListener { onItemClicked(audioFile) }
        }
    }

    class AudioFileDiffCallback : DiffUtil.ItemCallback<AudioFile>() {
        override fun areItemsTheSame(oldItem: AudioFile, newItem: AudioFile): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: AudioFile, newItem: AudioFile): Boolean {
            return oldItem == newItem
        }
    }
}
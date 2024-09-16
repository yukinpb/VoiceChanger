package com.example.voicechanger.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.voicechanger.databinding.ItemVoiceChangerBinding
import com.example.voicechanger.model.VoiceChangerItem
import com.example.voicechanger.util.Constants
import com.example.voicechanger.util.loadImage
import com.example.voicechanger.util.setOnSafeClickListener

class VoiceChangerAdapter(
    private val onItemClick: (VoiceChangerItem) -> Unit
) : ListAdapter<VoiceChangerItem, VoiceChangerAdapter.VoiceChangerViewHolder>(DiffCallback()) {

    private var selectedItemId: Int = Constants.VoiceEffects.NONE

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoiceChangerViewHolder {
        val binding = ItemVoiceChangerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VoiceChangerViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: VoiceChangerViewHolder, position: Int) {
        holder.bind(getItem(position), getItem(position).id == selectedItemId)
    }

    inner class VoiceChangerViewHolder(
        private val binding: ItemVoiceChangerBinding,
        private val onItemSelected: (VoiceChangerItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: VoiceChangerItem, isSelected: Boolean) {
            binding.title.text = item.text
            binding.icon.loadImage(item.imageRes, true)
            binding.llVoiceChanger.isSelected = isSelected
            binding.root.setOnSafeClickListener {
                val previousSelectedItemId = selectedItemId
                selectedItemId = item.id
                notifyItemChanged(currentList.indexOfFirst { it.id == previousSelectedItemId })
                notifyItemChanged(currentList.indexOfFirst { it.id == selectedItemId })
            }
            if (isSelected) {
                onItemSelected(item)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<VoiceChangerItem>() {
        override fun areItemsTheSame(oldItem: VoiceChangerItem, newItem: VoiceChangerItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: VoiceChangerItem, newItem: VoiceChangerItem): Boolean {
            return oldItem == newItem
        }
    }
}
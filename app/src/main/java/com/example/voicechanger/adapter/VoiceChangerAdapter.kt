package com.example.voicechanger.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.voicechanger.databinding.ItemVoiceChangerBinding
import com.example.voicechanger.model.VoiceChangerItem
import com.example.voicechanger.util.loadImage

class VoiceChangerAdapter(
    private val onItemClick: (VoiceChangerItem) -> Unit
) : ListAdapter<VoiceChangerItem, VoiceChangerAdapter.VoiceChangerViewHolder>(DiffCallback()) {

    private var selectedItemId: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoiceChangerViewHolder {
        val binding = ItemVoiceChangerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VoiceChangerViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: VoiceChangerViewHolder, position: Int) {
        holder.bind(getItem(position), getItem(position).id == selectedItemId)
    }

    inner class VoiceChangerViewHolder(
        private val binding: ItemVoiceChangerBinding,
        private val onItemClick: (VoiceChangerItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: VoiceChangerItem, isSelected: Boolean) {
            binding.title.text = item.text
            binding.icon.loadImage(item.imageRes, true)
            binding.cardViewVoiceChanger.isSelected = isSelected
            binding.root.setOnClickListener {
                val previousSelectedItemId = selectedItemId
                selectedItemId = item.id
                if (previousSelectedItemId != null) {
                    notifyItemChanged(currentList.indexOfFirst { it.id == previousSelectedItemId })
                }
                notifyItemChanged(currentList.indexOfFirst { it.id == selectedItemId })
                onItemClick(item)
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
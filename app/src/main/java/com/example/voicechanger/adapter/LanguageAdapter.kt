package com.example.voicechanger.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.voicechanger.databinding.ItemLanguageBinding
import com.example.voicechanger.model.Language

class LanguageAdapter(
    private val languages: List<Language>,
    private val selectedLanguage: Language?,
    private val onLanguageSelected: (Language) -> Unit
) : RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
        val binding = ItemLanguageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LanguageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
        holder.bind(languages[position], languages[position] == selectedLanguage)
    }

    override fun getItemCount(): Int = languages.size

    inner class LanguageViewHolder(private val binding: ItemLanguageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(language: Language, isSelected: Boolean) {
            binding.ivFlag.setImageResource(language.imageId)
            binding.tvLanguage.text = language.languageName
            binding.root.isSelected = isSelected
            binding.root.setOnClickListener { onLanguageSelected(language) }
        }
    }
}
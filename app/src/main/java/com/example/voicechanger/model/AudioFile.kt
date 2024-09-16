package com.example.voicechanger.model

data class AudioFile(
    val name: String,
    val time: String,
    val size: String,
    val filePath: String? = null,
    val lastModifier: Long = 0
)
package com.example.voicechanger.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AudioFile(
    val name: String,
    val time: String,
    val size: String,
    val filePath: String,
    val lastModifier: Long
) : Parcelable
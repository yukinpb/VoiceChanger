package com.example.voicechanger.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VoiceChangerItem(
    val id: Int,
    val text: String,
    val imageRes: Int
) : Parcelable
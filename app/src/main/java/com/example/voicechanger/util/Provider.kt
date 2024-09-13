package com.example.voicechanger.util

import com.example.voicechanger.R
import com.example.voicechanger.fragment.IntroduceFragment
import com.example.voicechanger.model.VoiceChangerItem

object IntroduceFragmentProvider {
    fun getFragments(): List<IntroduceFragment> {
        return listOf(
            IntroduceFragment.newInstance(R.mipmap.bg_tutorial_1, R.string.title_tutorial_1, R.string.content_tutorial_1),
            IntroduceFragment.newInstance(R.mipmap.bg_tutorial_2, R.string.title_tutorial_2, R.string.content_tutorial_2),
            IntroduceFragment.newInstance(R.mipmap.bg_tutorial_3, R.string.title_tutorial_3, R.string.content_tutorial_3),
            IntroduceFragment.newInstance(R.mipmap.bg_tutorial_4, R.string.title_tutorial_4, R.string.content_tutorial_4)
        )
    }
}

object SoundEffectProvider {
    fun getSoundEffectItems(): List<VoiceChangerItem> {
        return listOf(
            VoiceChangerItem(Constants.NONE, "None", R.mipmap.img_voice_changer_0),
            VoiceChangerItem(Constants.RADIO, "Radio", R.mipmap.img_voice_changer_1),
            VoiceChangerItem(Constants.CHIPMUNK, "Chipmunk", R.mipmap.img_voice_changer_2),
            VoiceChangerItem(Constants.ROBOT, "Robot", R.mipmap.img_voice_changer_3),
            VoiceChangerItem(Constants.CAVE, "Cave", R.mipmap.img_voice_changer_4)
        )
    }
}

object AmbientSoundProvider {
    fun getSoundEffectItems(): List<VoiceChangerItem> {
        return listOf()
    }
}
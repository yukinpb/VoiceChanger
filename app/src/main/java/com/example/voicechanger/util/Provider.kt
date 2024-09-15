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
            VoiceChangerItem(Constants.VoiceEffects.NONE, "None", R.mipmap.img_sound_effect_0),
            VoiceChangerItem(Constants.VoiceEffects.RADIO, "Radio", R.mipmap.img_sound_effect_1),
            VoiceChangerItem(Constants.VoiceEffects.CHIPMUNK, "Chipmunk", R.mipmap.img_sound_effect_2),
            VoiceChangerItem(Constants.VoiceEffects.ROBOT, "Robot", R.mipmap.img_sound_effect_3),
            VoiceChangerItem(Constants.VoiceEffects.CAVE, "Cave", R.mipmap.img_sound_effect_4)
        )
    }
}

object AmbientSoundProvider {
    fun getAmbientSoundItems(): List<VoiceChangerItem> {
        return listOf(
            VoiceChangerItem(Constants.VoiceEffects.NONE, "None", R.mipmap.img_ambient_sound_0),
            VoiceChangerItem(R.raw.clap, "Clap", R.mipmap.img_ambient_sound_1),
            VoiceChangerItem(R.raw.waterdrops, "Water Drop", R.mipmap.img_ambient_sound_2),
            VoiceChangerItem(R.raw.yeehaw, "Yeehaw", R.mipmap.img_ambient_sound_3),
        )
    }
}
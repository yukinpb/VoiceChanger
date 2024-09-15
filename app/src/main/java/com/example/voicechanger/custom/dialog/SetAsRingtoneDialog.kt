package com.example.voicechanger.custom.dialog

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.example.voicechanger.databinding.DialogSetAsRingtoneBinding
import com.example.voicechanger.util.Constants.RingtoneOptions
import com.example.voicechanger.util.toast
import java.io.File
import java.io.IOException
import java.util.Locale

class SetAsRingtoneDialog(
    private val audioFilePath: String
) : DialogFragment() {

    private var binding: DialogSetAsRingtoneBinding? = null
    private var selectedOption: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        binding = DialogSetAsRingtoneBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            btnRingtone.setOnClickListener { selectOption(RingtoneOptions.RINGTONE) }
            btnAlarm.setOnClickListener { selectOption(RingtoneOptions.ALARM) }
            btnNotification.setOnClickListener { selectOption(RingtoneOptions.NOTIFICATION) }

            buttonCancel.setOnClickListener { dismiss() }
            buttonOk.setOnClickListener {
                selectedOption?.let { option ->
                    setRingtone(option)
                }

                dismiss()
            }
        }
    }

    private fun selectOption(option: String) {
        selectedOption = option
        binding?.apply {
            btnRingtone.isSelected = option == RingtoneOptions.RINGTONE
            btnAlarm.isSelected = option == RingtoneOptions.ALARM
            btnNotification.isSelected = option == RingtoneOptions.NOTIFICATION
        }
    }

    private fun setRingtone(option: String) {
        val file = File(audioFilePath)
        if (!file.exists()) return

        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DATA, file.absolutePath)
            put(MediaStore.MediaColumns.TITLE, "My Ringtone")
            put(MediaStore.MediaColumns.MIME_TYPE, "audio/mpeg")
            put(MediaStore.Audio.Media.IS_RINGTONE, true)
            put(MediaStore.Audio.Media.IS_NOTIFICATION, false)
            put(MediaStore.Audio.Media.IS_ALARM, false)
            put(MediaStore.Audio.Media.IS_MUSIC, false)
        }

        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val context: Context = requireContext()
        context.contentResolver.delete(
            uri,
            MediaStore.MediaColumns.DATA + "=\"" + file.absolutePath + "\"",
            null
        )
        val newUri = context.contentResolver.insert(uri, values)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(context)) {
                try {
                    RingtoneManager.setActualDefaultRingtoneUri(
                        context,
                        getRingtoneType(option),
                        newUri
                    )
                    context.toast("Set as ${option.lowercase(Locale.ROOT)} successfully")
                } catch (e: IOException) {
                    e.printStackTrace()
                    context.toast("Device does not support this feature")
                }
            } else {
                context.toast("Device does not support this feature")
            }
        } else {
            try {
                RingtoneManager.setActualDefaultRingtoneUri(
                    context,
                    getRingtoneType(option),
                    newUri
                )
                context.toast("Set as ${option.lowercase(Locale.ROOT)} successfully")
            } catch (e: IOException) {
                e.printStackTrace()
                context.toast("Device does not support this feature")
            }
        }
    }

    override fun onResume() {
        super.onResume()

        val layoutParams = dialog?.window?.attributes
        layoutParams?.width = ViewGroup.LayoutParams.MATCH_PARENT
        layoutParams?.horizontalMargin = 0.1f
        dialog?.window?.attributes = layoutParams
    }

    private fun getRingtoneType(option: String): Int {
        return when (option) {
            RingtoneOptions.RINGTONE -> RingtoneManager.TYPE_RINGTONE
            RingtoneOptions.ALARM -> RingtoneManager.TYPE_ALARM
            RingtoneOptions.NOTIFICATION -> RingtoneManager.TYPE_NOTIFICATION
            else -> RingtoneManager.TYPE_RINGTONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
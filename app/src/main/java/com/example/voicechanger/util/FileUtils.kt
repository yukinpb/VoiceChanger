package com.example.voicechanger.util

import android.content.Context
import android.os.Environment
import java.io.File

const val JPEG = ".jpg"
class FileUtils {
    fun createFileInStorage(context: Context, fileName: String): File {
        val timeStamp: String = System.currentTimeMillis().toString() + JPEG
        val name = fileName.ifBlank { timeStamp }
        return File(getAppFilesDir(context), name)
    }

    fun createFileInExternalStorage(context: Context, fileName: String): File {
        val timeStamp: String = System.currentTimeMillis().toString() + JPEG
        val name = fileName.ifBlank { timeStamp }
        return File(getAppExternalFilesDir(context), name)
    }

    private fun getAppFilesDir(context: Context): File? {
        val file = context.filesDir
        if (file != null && !file.exists()) {
            file.mkdirs()
        }
        return file
    }

    private fun getAppExternalFilesDir(context: Context): File? {
        val file = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (file != null && !file.exists()) {
            file.mkdirs()
        }
        return file
    }
}
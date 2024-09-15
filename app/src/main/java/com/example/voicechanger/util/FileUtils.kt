package com.example.voicechanger.util

import android.content.Context
import android.media.MediaMetadataRetriever
import android.os.Environment
import java.io.File

const val JPEG = ".jpg"
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

fun File.getDuration(): String {
    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(this.absolutePath)
    val durationInMillis = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toInt() ?: 0
    retriever.release()
    return (durationInMillis / 1000).toDurationString()
}

fun File.getSize(): String {
    val size = this.length() / 1024.0f
    return String.format("%.2f KB", size)
}
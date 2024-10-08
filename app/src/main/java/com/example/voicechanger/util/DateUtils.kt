package com.example.voicechanger.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun String.toDate(format: String): Date? {
    val dateFormatter = SimpleDateFormat(format, Locale.US)
    return try {
        dateFormatter.parse(this)
    } catch (e: ParseException) {
        null
    }
}

fun Date.toString(format: String): String {
    val dateFormatter = SimpleDateFormat(format, Locale.US)
    return dateFormatter.format(this)
}

fun String.getCurrentDayName(): String {
    val cal = Calendar.getInstance()
    val date = toDate("yyyy/MM/dd")
    if (date != null) {
        cal.time = date
    }
    return when (cal[Calendar.DAY_OF_WEEK]) {
        Calendar.MONDAY -> "Mon"
        Calendar.TUESDAY -> "Tue"
        Calendar.WEDNESDAY -> "Wed"
        Calendar.THURSDAY -> "Thu"
        Calendar.FRIDAY -> "Fri"
        Calendar.SATURDAY -> "Sat"
        else -> "Sun"
    }
}

fun Int.toDurationString(): String {
    val minutes = this / 60
    val seconds = this % 60
    return String.format("%02d:%02d", minutes, seconds)
}